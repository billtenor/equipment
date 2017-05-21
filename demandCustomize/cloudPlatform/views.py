#coding:utf-8

from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render, render_to_response
from django.http import HttpResponse
from django.utils.safestring import SafeString
from django.template import context
import json
from django.core import serializers
import datetime
from .models import *
import subprocess
from qingstor.sdk.service.qingstor import QingStor
from qingstor.sdk.config import Config

config = Config('IKLQINFIKLQKWRMCJVLN', 'HcTZxhXDTSktGKsDnIH9nGTprs1XIJUuSaM9Cu1x')
qingstor = QingStor(config)

bucketName = 'equipment'
demandid_cache = ''
visualization_draw_id_cache = ''
zoneName = 'pek3a'
qingstorURL = 'https://'+zoneName+'.qingstor.com/'+bucketName+'/visualization'

# Create your views here.


def index(request):
    return render(request, 'cloudPlatform/index.html', {'page_name':'index'})


def demandmodule(request):
    typelist = TagType.objects.values('TagTypeName')
    tooltypelist = AnalysisToolType.objects.exclude(AnalysisToolTypeName='virtual tool type').values('AnalysisToolTypeName')
    return render(request, 'cloudPlatform/demandmodule.html', {'page_name': 'demandmodule', 'typelist': typelist, 'tooltypelist': tooltypelist})


@csrf_exempt
def demandtemplateShow(request):
    global demandid_cache
    if request.method == 'POST':
        if 'templateid' in request.POST.keys():
            delete_templateid = request.POST['templateid']
            DemandTemplate.objects.filter(DemandTemplateID=delete_templateid).delete()
        demandid = request.POST['demandid']
        demandid_cache = demandid
    else:
        demandid = demandid_cache
    select_demand = Demand.objects.get(DemandID=demandid)
    select_demandtemplate = select_demand.demandtemplate_set.all()
    mid_select_demandtag = select_demand.demand_has_tag_set.all()
    select_demandtag = []
    for i in mid_select_demandtag:
        select_demandtag.append(i.Tag_TagID.TagLabel)
    context = {
        'page_name': 'demandtemplateShow',
        'demand': select_demand,
        'demandtag': select_demandtag,
        'demandtemplate': select_demandtemplate
    }
    return render(request, 'cloudPlatform/demandtemplateShow.html', context)


@csrf_exempt
def adddemandmodule(request):
    if request.method == 'POST':
        demandid = int(request.POST['demandid'])
        new_DemandTemplateName = request.POST['name']
        new_DemandTemplatePara = request.POST['para']
        new_DemandTemplateDescrip = request.POST['descrip']
        new_Demand_DemandID = Demand.objects.get(DemandID=demandid)
        r = DemandTemplate.objects.create(DemandTemplateName=new_DemandTemplateName, DemandTemplatePara=new_DemandTemplatePara,
                                          DemandTemplateDescrip=new_DemandTemplateDescrip, Demand_DemandID=new_Demand_DemandID)
        t = AnalysisTool.objects.get(AnalysisToolName='virtual tool')
        DemandTemplateNode.objects.create(NodeType='start', AnalysisToolPara='', DemandTemplate_DemandTemplateID=r,
                                          AnalysisTool_AnalysisToolID=t)
        DemandTemplateNode.objects.create(NodeType='end', AnalysisToolPara='', DemandTemplate_DemandTemplateID=r,
                                          AnalysisTool_AnalysisToolID=t)
        data = str(r.DemandTemplateID)
        return HttpResponse(data)


def dag_before_test(request):
    return render(request, 'cloudPlatform/dag_before_test.html')


@csrf_exempt
def selectTagType(request):
    if request.method == 'POST':
        selectedTagType = request.POST['selectedTagType']
        print selectedTagType
        if selectedTagType == u'所有类型':
            tag_has_selectedtype = Tag.objects.values('TagLabel')
        else:
            tag_has_selectedtype = Tag.objects.filter(TagType_TagTypeID__TagTypeName__exact=selectedTagType).values('TagLabel')
        data = json.dumps(list(tag_has_selectedtype))
        return HttpResponse(data)


def tool_to_dict(tool):
    tool_dict = {'AnalysisToolName': tool.AnalysisToolName, 'AnalysisToolDescrip': tool.AnalysisToolDescrip,
                 'AnalysisToolPlatform': tool.AnalysisToolPlatform, 'AnalysisToolVersion': tool.AnalysisToolVersion,
                 'CreateTime': tool.CreateTime.strftime("%Y-%m-%d-%H"),
                 'LastModifyTime': tool.LastModifyTime.strftime("%Y-%m-%d-%H")}
    return tool_dict


@csrf_exempt
def searchTool(request):
    if request.method == 'POST':
        data = request.POST
        tags = data['tags'].split(',')
        tooltype = data['tooltype']
        toolname = data['toolname']
        if toolname != '':
            condition_1 = '1'
        else:
            condition_1 = '0'
        if tooltype == 'All types':
            condition_2 = '1'
        elif tooltype == 'Select Tooltype':
            condition_2 = '0'
        else:
            condition_2 = '2'
        if tags[0] != '':
            condition_3 = '1'
        else:
            condition_3 = '0'
        condition = condition_1 + condition_2 + condition_3
        if condition == '000':
            data = json.dumps([])
            return HttpResponse(data)
        elif condition == '001' or condition == '011':
            tool_list = list(AnalysisTool.objects.all())
        elif condition == '010':
            tool_list = list(AnalysisTool.objects.all())
            tool_dict_list = []
            for i in tool_list:
                tool_dict_list.append(tool_to_dict(i))
            data = json.dumps(tool_dict_list)
            return HttpResponse(data)
        elif condition == '020':
            tool_list = list(AnalysisTool.objects.filter(AnalysisToolType_AnalysisToolTypeID__AnalysisToolTypeName__exact=tooltype))
            tool_dict_list = []
            for i in tool_list:
                tool_dict_list.append(tool_to_dict(i))
            data = json.dumps(tool_dict_list)
            return HttpResponse(data)
        elif condition == '021':
            tool_list = list(AnalysisTool.objects.filter(AnalysisToolType_AnalysisToolTypeID__AnalysisToolTypeName__exact=tooltype))
        elif condition == '100'or condition == '110':
            tool_list = list(AnalysisTool.objects.filter(AnalysisToolName__contains=toolname))
            tool_dict_list = []
            for i in tool_list:
                tool_dict_list.append(tool_to_dict(i))
            data = json.dumps(tool_dict_list)
            return HttpResponse(data)
        elif condition == '101' or condition == '111':
            tool_list = list(AnalysisTool.objects.filter(AnalysisToolName__contains=toolname))
        elif condition == '120':
            tool_list = list(AnalysisTool.objects.filter(AnalysisToolType_AnalysisToolTypeID__AnalysisToolTypeName__exact=tooltype,
                                                    AnalysisToolName__contains=toolname))
            tool_dict_list = []
            for i in tool_list:
                tool_dict_list.append(tool_to_dict(i))
            data = json.dumps(tool_dict_list)
            return HttpResponse(data)
        elif condition == '121':
            tool_list = list(AnalysisTool.objects.filter(AnalysisToolType_AnalysisToolTypeID__AnalysisToolTypeName__exact=tooltype,
                                                         AnalysisToolName__contains=toolname))
        t = range(len(tags))
        for i in tool_list:
            k = i.analysistool_has_tag_set.all()
            tool_alltags_list = []
            for j in k:
                tool_alltags_list.append(j.Tag_TagID.TagLabel)
            for s in t:
                if tags[s] not in tool_alltags_list:
                    tool_list.remove(i)
                    break
        tool_dict_list = []
        for i in tool_list:
            tool_dict_list.append(tool_to_dict(i))
        data = json.dumps(tool_dict_list)
        return HttpResponse(data)

@csrf_exempt
def AnalysisToolTypeShow(request):
    if request.method == 'POST':
        if request.POST['db_method'] == 'db_delete':
            nid = request.POST['db_id']
            try:
                AnalysisToolType.objects.filter(AnalysisToolTypeID=nid).delete()
            except models.ProtectedError:
                return render(request, "cloudPlatform/666.html")
        if request.POST['db_method'] == 'db_modify':
            nid = request.POST['db_id']
            new_TypeName = request.POST['new_TypeName']
            new_TypeDescrip = request.POST['new_TypeDescrip']
            AnalysisToolType.objects.filter(AnalysisToolTypeID=nid).update(AnalysisToolTypeName=new_TypeName, AnalysisToolTypeDescrip=new_TypeDescrip)
        if request.POST['db_method'] == 'db_add':
            new_TypeName = request.POST['new_TypeName']
            new_TypeDescrip = request.POST['new_TypeDescrip']
            if new_TypeName != '':
                AnalysisToolType.objects.create(AnalysisToolTypeName=new_TypeName, AnalysisToolTypeDescrip=new_TypeDescrip)
            else:
                AnalysisToolType.error(request, 'not valid input', extra_tags='bg-warning text-warning')
    data_list = AnalysisToolType.objects.all()
    return render(request, 'cloudPlatform/AnalysisToolTypeShow.html', {'data_list': data_list})

@csrf_exempt
def TagTypeShow(request):
    if request.method == 'POST':
        if request.POST['db_method'] == 'db_delete':
            nid = request.POST['db_id']
            try:
                TagType.objects.filter(TagTypeID=nid).delete()
            except models.ProtectedError:
                return render(request, "cloudPlatform/666.html")
        if request.POST['db_method'] == 'db_modify':
            nid = request.POST['db_id']
            new_TypeName = request.POST['new_TagTypeName']
            new_TypeDescrip = request.POST['new_TypeDescrip']
            TagType.objects.filter(TagTypeID=nid).update(TagTypeName=new_TypeName, TagTypeDescrip=new_TypeDescrip)
        if request.POST['db_method'] == 'db_add':
            new_TypeName = request.POST['new_TagTypeName']
            new_TypeDescrip = request.POST['new_TypeDescrip']
            if new_TypeName != '':
                TagType.objects.create(TagTypeName=new_TypeName, TagTypeDescrip=new_TypeDescrip)
            else:
                TagType.error(request, 'not valid input', extra_tags='bg-warning text-warning')
    data_list = TagType.objects.all()
    return render(request, 'cloudPlatform/TagTypeShow.html', {'data_list': data_list})

@csrf_exempt
def TagShow(request):
    if request.method == 'POST':
        if request.POST['db_method'] == 'db_delete':
            nid = request.POST['db_id']
            try:
                Tag.objects.filter(TagID=nid).delete()
            except models.ProtectedError:
                return render(request, "cloudPlatform/666.html")
        if request.POST['db_method'] == 'db_modify':
            nid = request.POST['db_id']
            new_TagTabel = request.POST['new_TagLabel']
            new_TagDescrip = request.POST['new_TagDescrip']
            mid_TagType = request.POST['new_TagType']
            new_TagType = TagType.objects.get(TagTypeName=mid_TagType)
            Tag.objects.filter(TagID=nid).update(TagLabel=new_TagTabel, TagDescrip=new_TagDescrip,TagType_TagTypeID=new_TagType)
            Tag.objects.get(TagID=nid).save()
        if request.POST['db_method'] == 'db_add':
            new_TagTabel = request.POST['new_TagLabel']
            new_TagDescrip = request.POST['new_TagDescrip']
            mid_TagType = request.POST['new_TagType']
            new_TagType = TagType.objects.get(TagTypeName=mid_TagType)
            if new_TagTabel != '':
                Tag.objects.create(TagLabel=new_TagTabel,TagDescrip=new_TagDescrip,TagType_TagTypeID=new_TagType)
            else:
                Tag.error(request, 'not valid input', extra_tags='bg-warning text-warning')
    data_list = Tag.objects.all()
    TagTypeList = TagType.objects.all()
    return render(request, 'cloudPlatform/TagShow.html', {'data_list': data_list,'TagTypeList': TagTypeList})

@csrf_exempt
def getTags(request):
    if request.method == 'POST':
        tags = Tag.objects.values('TagLabel')
        data = json.dumps(list(tags))
        return HttpResponse(data)


@csrf_exempt
def getAnalysisToolTags(request):
    if request.method == 'POST':
        tools = AnalysisTool.objects.all()
        tool_tags = {}
        for i in tools:
            tags = []
            r_tags = i.analysistool_has_tag_set.all()
            for j in r_tags:
                tags.append(j.Tag_TagID.TagLabel)
            tool_tags[i.AnalysisToolID] = tags
        data = json.dumps(tool_tags)
        return HttpResponse(data)

@csrf_exempt
def getDemandTags(request):
    if request.method == 'POST':
        tools = Demand.objects.all()
        tool_tags = {}
        for i in tools:
            tags = []
            r_tags = i.demand_has_tag_set.all()
            for j in r_tags:
                tags.append(j.Tag_TagID.TagLabel)
            tool_tags[i.DemandID] = tags
        data = json.dumps(tool_tags)
        return HttpResponse(data)

@csrf_exempt
def getVisualizationToolTags(request):
    if request.method == 'POST':
        tools = VisualizationTool.objects.all()
        tool_tags = {}
        for i in tools:
            tags = []
            r_tags = i.visualizationtool_has_tag_set.all()
            for j in r_tags:
                tags.append(j.Tag_TagID.TagLabel)
            tool_tags[i.VisualizationToolID] = tags
        data = json.dumps(tool_tags)
        return HttpResponse(data)


@csrf_exempt
def AnalysisToolShow(request):
    if request.method == 'POST':
        if request.POST['db_method'] == 'db_delete':
            nid = request.POST['db_id']
            try:
                AnalysisTool.objects.filter(AnalysisToolID=nid).delete()
            except models.ProtectedError:
                return render(request, "cloudPlatform/666.html")
        if request.POST['db_method'] == 'db_modify':
            nid = request.POST['db_id']
            new_AnalysisToolName = request.POST['new_AnalysisToolName']
            new_AnalysisToolDescrip = request.POST['new_AnalysisToolDescrip']
            new_AnalysisToolPlatform = request.POST['new_AnalysisToolPlatform']
            new_AnalysisToolVersion = request.POST['new_AnalysisToolVersion']
            mid_AnalysisToolType = request.POST['new_AnalysisToolType']
            new_AnalysisToolType = AnalysisToolType.objects.get(AnalysisToolTypeName=mid_AnalysisToolType)
            AnalysisTool.objects.filter(AnalysisToolID=nid).update(AnalysisToolName=new_AnalysisToolName, AnalysisToolDescrip=new_AnalysisToolDescrip,AnalysisToolPlatform=new_AnalysisToolPlatform, AnalysisToolVersion=new_AnalysisToolVersion,AnalysisToolType_AnalysisToolTypeID=new_AnalysisToolType)
            AnalysisTool.objects.get(AnalysisToolID=nid).save()
            mid_object = AnalysisTool.objects.get(AnalysisToolID=nid)
            mid_AnalysisToolTag = request.POST['tag_list'].split(',')
            AnalysisTool_has_Tag.objects.filter(AnalysisToolID_TagID__contains=new_AnalysisToolName).delete()
            for i in mid_AnalysisToolTag:
                new_tag = Tag.objects.get(TagLabel=i)
                AnalysisTool_has_Tag.objects.create(AnalysisTool_AnalysisToolID=mid_object, Tag_TagID=new_tag)
        if request.POST['db_method'] == 'db_add':
            new_AnalysisToolName = request.POST['new_AnalysisToolName']
            new_AnalysisToolDescrip = request.POST['new_AnalysisToolDescrip']
            new_AnalysisToolPlatform = request.POST['new_AnalysisToolPlatform']
            new_AnalysisToolVersion = request.POST['new_AnalysisToolVersion']
            mid_AnalysisToolTag = request.POST['tag_list'].split(',')
            mid_AnalysisToolType = request.POST['new_AnalysisToolType']
            new_AnalysisToolType = AnalysisToolType.objects.get(AnalysisToolTypeName=mid_AnalysisToolType)
            if new_AnalysisToolName != '' and mid_AnalysisToolTag != '':
                new_tool = AnalysisTool.objects.create(AnalysisToolName=new_AnalysisToolName,AnalysisToolDescrip=new_AnalysisToolDescrip,AnalysisToolPlatform=new_AnalysisToolPlatform,AnalysisToolVersion=new_AnalysisToolVersion,AnalysisToolType_AnalysisToolTypeID=new_AnalysisToolType)
                for i in mid_AnalysisToolTag:
                    new_tag = Tag.objects.get(TagLabel=i)
                    AnalysisTool_has_Tag.objects.create(AnalysisTool_AnalysisToolID=new_tool, Tag_TagID=new_tag)
            else:
                AnalysisTool.error(request, 'not valid input', extra_tags='bg-warning text-warning')
    data_list = AnalysisTool.objects.all()
    AnalysisToolTypeList = AnalysisToolType.objects.all()
    return render(request, 'cloudPlatform/AnalysisToolShow.html', {'data_list': data_list,'AnalysisToolTypeList': AnalysisToolTypeList})

@csrf_exempt
def DemandTypeShow(request):
    if request.method == 'POST':
        if request.POST['db_method'] == 'db_delete':
            nid = request.POST['db_id']
            try:
                DemandType.objects.filter(DemandTypeID=nid).delete()
            except models.ProtectedError:
                return render(request, "cloudPlatform/666.html")
        if request.POST['db_method'] == 'db_modify':
            nid = request.POST['db_id']
            new_TypeName = request.POST['new_DemandTypeName']
            new_TypeDescrip = request.POST['new_DemandTypeDescrip']
            DemandType.objects.filter(DemandTypeID=nid).update(DemandTypeName=new_TypeName, DemandTypeDescrip=new_TypeDescrip)
        if request.POST['db_method'] == 'db_add':
            new_TypeName = request.POST['new_DemandTypeName']
            new_TypeDescrip = request.POST['new_DemandTypeDescrip']
            if new_TypeName != '':
                DemandType.objects.create(DemandTypeName=new_TypeName, DemandTypeDescrip=new_TypeDescrip)
            else:
                DemandType.error(request, 'not valid input', extra_tags='bg-warning text-warning')
    data_list = DemandType.objects.all()
    return render(request, 'cloudPlatform/DemandTypeShow.html', {'data_list': data_list})

@csrf_exempt
def DemandShow(request):
    if request.method == 'POST':
        if request.POST['db_method'] == 'db_delete':
            nid = request.POST['db_id']
            try:
                Demand.objects.filter(DemandID=nid).delete()
            except models.ProtectedError:
                return render(request, "cloudPlatform/666.html")
        if request.POST['db_method'] == 'db_modify':
            nid = request.POST['db_id']
            new_DemandName = request.POST['new_DemandName']
            new_DemandDescrip = request.POST['new_DemandDescrip']
            mid_DemandType = request.POST['new_DemandType']
            new_DemandType = DemandType.objects.get(DemandTypeName=mid_DemandType)
            Demand.objects.filter(DemandID=nid).update(DemandName=new_DemandName, DemandDescrip=new_DemandDescrip,DemandType_DemandTypeID=new_DemandType)
            Demand.objects.get(DemandID=nid).save()
            mid_object = Demand.objects.get(DemandID=nid)
            mid_DemandTag = request.POST['tag_list'].split(',')
            Demand_has_Tag.objects.filter(DemandID_TagID__contains=new_DemandName).delete()
            for i in mid_DemandTag:
                new_tag = Tag.objects.get(TagLabel=i)
                Demand_has_Tag.objects.create(Demand_DemandID=mid_object, Tag_TagID=new_tag)
        if request.POST['db_method'] == 'db_add':
            new_DemandName = request.POST['new_DemandName']
            new_DemandDescrip = request.POST['new_DemandDescrip']
            mid_DemandType = request.POST['new_DemandType']
            new_DemandType = DemandType.objects.get(DemandTypeName=mid_DemandType)
            mid_DemandTag = request.POST['tag_list'].split(',')
            if new_DemandName != ''and mid_DemandTag != '':
                new_tool = Demand.objects.create(DemandName=new_DemandName,DemandDescrip=new_DemandDescrip,DemandType_DemandTypeID=new_DemandType)
                for i in mid_DemandTag:
                    new_tag = Tag.objects.get(TagLabel=i)
                    Demand_has_Tag.objects.create(Demand_DemandID=new_tool, Tag_TagID=new_tag)
            else:
                Demand.error(request, 'not valid input', extra_tags='bg-warning text-warning')
    data_list = Demand.objects.all()
    DemandTypeList = DemandType.objects.all()
    return render(request, 'cloudPlatform/DemandShow.html', {'data_list': data_list,'DemandTypeList': DemandTypeList})

@csrf_exempt
def VisToolTypeShow(request):
    if request.method == 'POST':
        if request.POST['db_method'] == 'db_delete':
            nid = request.POST['db_id']
            try:
                VisualizationToolType.objects.filter(VisualizationToolTypeID=nid).delete()
            except models.ProtectedError:
                return render(request, "cloudPlatform/666.html")
        if request.POST['db_method'] == 'db_modify':
            nid = request.POST['db_id']
            new_TypeName = request.POST['new_VisToolTypeName']
            new_TypeDescrip = request.POST['new_VisToolTypeDescrip']
            VisualizationToolType.objects.filter(VisualizationToolTypeID=nid).update(VisualizationToolTypeName=new_TypeName, VisualizationToolTypeDescrip=new_TypeDescrip)
        if request.POST['db_method'] == 'db_add':
            new_TypeName = request.POST['new_VisToolTypeName']
            new_TypeDescrip = request.POST['new_VisToolTypeDescrip']
            if new_TypeName != '':
                VisualizationToolType.objects.create(VisualizationToolTypeName=new_TypeName, VisualizationToolTypeDescrip=new_TypeDescrip)
            else:
                VisualizationToolType.error(request, 'not valid input', extra_tags='bg-warning text-warning')
    data_list = VisualizationToolType.objects.all()
    return render(request, 'cloudPlatform/VisToolTypeShow.html', {'data_list': data_list})

@csrf_exempt
def VisToolShow(request):
    if request.method == 'POST':
        if request.POST['db_method'] == 'db_delete':
            nid = request.POST['db_id']
            try:
                VisualizationTool.objects.filter(VisualizationToolID=nid).delete()
            except models.ProtectedError:
                return render(request, "cloudPlatform/666.html")
        if request.POST['db_method'] == 'db_modify':
            nid = request.POST['db_id']
            new_VisToolName = request.POST['new_VisToolName']
            new_VisToolDescrip = request.POST['new_VisToolDescrip']
            new_VisToolVersion = request.POST['new_VisToolVersion']
            mid_VisToolType = request.POST['new_VisToolType']
            new_VisToolType = VisualizationToolType.objects.get(VisualizationToolTypeName=mid_VisToolType)
            VisualizationTool.objects.filter(VisualizationToolID=nid).update(VisualizationToolName=new_VisToolName, VisualizationToolDescrip=new_VisToolDescrip,VisualizationToolVersion=new_VisToolVersion,VisualizationToolType_VisualizationToolID=new_VisToolType)
            VisualizationTool.objects.get(VisualizationToolID=nid).save()
            mid_object = VisualizationTool.objects.get(VisualizationToolID=nid)
            mid_VisualizationToolTag = request.POST['tag_list'].split(',')
            VisualizationTool_has_Tag.objects.filter(VisualizationToolID_TagID__contains=new_VisToolName).delete()
            for i in mid_VisualizationToolTag:
                new_tag = Tag.objects.get(TagLabel=i)
                VisualizationTool_has_Tag.objects.create(VisualizationTool_VisualizationToolID=mid_object, Tag_TagID=new_tag)
        if request.POST['db_method'] == 'db_add':
            new_VisToolName = request.POST['new_VisToolName']
            new_VisToolDescrip = request.POST['new_VisToolDescrip']
            new_VisToolVersion = request.POST['new_VisToolVersion']
            mid_VisToolType = request.POST['new_VisToolType']
            new_VisToolType = VisualizationToolType.objects.get(VisualizationToolTypeName=mid_VisToolType)
            mid_VisualizationToolTag = request.POST['tag_list'].split(',')
            if new_VisToolName != ''and mid_VisualizationToolTag != '':
                new_tool = VisualizationTool.objects.create(VisualizationToolName=new_VisToolName,VisualizationToolDescrip=new_VisToolDescrip,VisualizationToolVersion=new_VisToolVersion,VisualizationToolType_VisualizationToolID=new_VisToolType)
                for i in mid_VisualizationToolTag:
                    new_tag = Tag.objects.get(TagLabel=i)
                    VisualizationTool_has_Tag.objects.create(VisualizationTool_VisualizationToolID=new_tool, Tag_TagID=new_tag)
            else:
                VisualizationTool.error(request, 'not valid input', extra_tags='bg-warning text-warning')
    data_list = VisualizationTool.objects.all()
    VisToolTypeList = VisualizationToolType.objects.all()
    return render(request, 'cloudPlatform/VisToolShow.html', {'data_list': data_list,'VisToolTypeList': VisToolTypeList})


def dag_to_dict(dagname, node, link):
    dag_dict = {'name': dagname, 'tool': {}, 'nodes': [], 'links': []}
    mid_node_transform = {}
    for i in node:
        node_type = i.NodeType
        tool_name = i.AnalysisTool_AnalysisToolID.AnalysisToolName
        if node_type == 'start' or node_type == 'end':
            tool_label = node_type
        else:
            if tool_name not in dag_dict['tool']:
                dag_dict['tool'][tool_name] = 1
            else:
                dag_dict['tool'][tool_name] += 1
            tool_index = dag_dict['tool'][tool_name]
            tool_label = i.AnalysisTool_AnalysisToolID.AnalysisToolName + '_' + str(tool_index)
        node_content = {'id': tool_label, 'value': {'label': tool_label, 'type': i.NodeType, 'para': i.AnalysisToolPara}}
        dag_dict['nodes'].append(node_content)
        mid_node_transform[i.DemandTemplateNodeID] = tool_label
    for i in link:
        u = mid_node_transform[i.FrontNodeID.DemandTemplateNodeID]
        v = mid_node_transform[i.BackNodeID.DemandTemplateNodeID]
        link_content = {'u': u, 'v': v, 'value': {'descrip': i.DataDescrip}}
        dag_dict['links'].append(link_content)
    return dag_dict


@csrf_exempt
def getDAG(request):
    if request.method == 'POST':
        dagid = request.POST['id']
        demandtemplate = DemandTemplate.objects.get(DemandTemplateID=dagid)
        node = demandtemplate.demandtemplatenode_set.all()
        link = demandtemplate.analysistoolconnect_set.all()
        dag_dict = dag_to_dict(demandtemplate.DemandTemplateName, node, link)
        data = json.dumps(dag_dict)
        return HttpResponse(data)

@csrf_exempt
def saveDAG(request):
    if request.method == 'POST':
        tid = int(request.POST['templateid'])
        dag = json.loads(request.POST['dag'])
        connect = {}
        AnalysisToolConnect.objects.filter(DemandTemplate_DemandTemplateID__DemandTemplateID=tid).delete()
        DemandTemplateNode.objects.filter(DemandTemplate_DemandTemplateID__DemandTemplateID=tid).delete()
        new_DemandTemplate = DemandTemplate.objects.get(DemandTemplateID=tid)
        for i in dag['nodes']:
            new_NodeType = i['value']['type']
            new_AnalysisToolPara = i['value']['para']
            if i['id'] == 'start' or i['id'] == 'end':
                new_AnalysisTool = AnalysisTool.objects.get(AnalysisToolName='virtual tool')
            else:
                mid_AnalysisTool = i['id'].split('_')[0]
                new_AnalysisTool = AnalysisTool.objects.get(AnalysisToolName=mid_AnalysisTool)
            new_node = DemandTemplateNode.objects.create(NodeType=new_NodeType, AnalysisToolPara=new_AnalysisToolPara,
                                              DemandTemplate_DemandTemplateID=new_DemandTemplate,
                                              AnalysisTool_AnalysisToolID=new_AnalysisTool)
            connect[i['id']] = new_node
        for i in dag['links']:
            new_FrontNodeID = connect[i['u']]
            new_BackNodeID = connect[i['v']]
            new_DataDescrip = i['value']['descrip']
            AnalysisToolConnect.objects.create(FrontNodeID=new_FrontNodeID, BackNodeID=new_BackNodeID,
                                               DemandTemplate_DemandTemplateID=new_DemandTemplate,
                                               DataDescrip=new_DataDescrip)
        return HttpResponse('success')


def visualizationtool_to_dict(tool):
    tool_dict = {'VisualizationToolName': tool.VisualizationToolName,
                 'VisualizationToolDescrip': tool.VisualizationToolDescrip,
                 'VisualizationToolVersion': tool.VisualizationToolVersion,
                 'CreateTime': tool.CreateTime.strftime("%Y-%m-%d-%H"),
                 'LastModifyTime': tool.LastModifyTime.strftime("%Y-%m-%d-%H")}
    return tool_dict


@csrf_exempt
def searchVisualizationTool(request):
    if request.method == 'POST':
        data = request.POST
        tags = data['tags'].split(',')
        tooltype = data['tooltype']
        toolname = data['toolname']
        if toolname != '':
            condition_1 = '1'
        else:
            condition_1 = '0'
        if tooltype == u'所有类型':
            condition_2 = '1'
        elif tooltype == u'选择工具类型':
            condition_2 = '0'
        else:
            condition_2 = '2'
        if tags[0] != '':
            condition_3 = '1'
        else:
            condition_3 = '0'
        condition = condition_1 + condition_2 + condition_3
        if condition == '000':
            data = json.dumps([])
            return HttpResponse(data)
        elif condition == '001' or condition == '011':
            tool_list = list(VisualizationTool.objects.all())
        elif condition == '010':
            tool_list = list(VisualizationTool.objects.all())
            tool_dict_list = []
            for i in tool_list:
                tool_dict_list.append(visualizationtool_to_dict(i))
            data = json.dumps(tool_dict_list)
            return HttpResponse(data)
        elif condition == '020':
            tool_list = list(VisualizationTool.objects.filter(VisualizationToolType_VisualizationToolID__VisualizationToolTypeName__exact=tooltype))
            tool_dict_list = []
            for i in tool_list:
                tool_dict_list.append(visualizationtool_to_dict(i))
            data = json.dumps(tool_dict_list)
            return HttpResponse(data)
        elif condition == '021':
            tool_list = list(VisualizationTool.objects.filter(VisualizationToolType_VisualizationToolID__VisualizationToolTypeName__exact=tooltype))
        elif condition == '100'or condition == '110':
            tool_list = list(VisualizationTool.objects.filter(VisualizationToolName__contains=toolname))
            tool_dict_list = []
            for i in tool_list:
                tool_dict_list.append(visualizationtool_to_dict(i))
            data = json.dumps(tool_dict_list)
            return HttpResponse(data)
        elif condition == '101' or condition == '111':
            tool_list = list(VisualizationTool.objects.filter(VisualizationToolName__contains=toolname))
        elif condition == '120':
            tool_list = list(VisualizationTool.objects.filter(VisualizationToolType_VisualizationToolID__VisualizationToolTypeName__exact=tooltype,
                                                              VisualizationToolName__contains=toolname))
            tool_dict_list = []
            for i in tool_list:
                tool_dict_list.append(visualizationtool_to_dict(i))
            data = json.dumps(tool_dict_list)
            return HttpResponse(data)
        elif condition == '121':
            tool_list = list(VisualizationTool.objects.filter(VisualizationToolType_VisualizationToolID__VisualizationToolTypeName__exact=tooltype,
                                                              VisualizationToolName__contains=toolname))
        t = range(len(tags))
        for i in tool_list:
            k = i.visualizationtool_has_tag_set.all()
            tool_alltags_list = []
            for j in k:
                tool_alltags_list.append(j.Tag_TagID.TagLabel)
            for s in t:
                if tags[s] not in tool_alltags_list:
                    tool_list.remove(i)
                    break
        tool_dict_list = []
        for i in tool_list:
            tool_dict_list.append(visualizationtool_to_dict(i))
        data = json.dumps(tool_dict_list)
        return HttpResponse(data)


def resultfile_to_dict(resultfile):
    resultfile_dict = {'ResultFileID': resultfile.ResultFileID,
                       'ResultFileName': resultfile.ResultFileName,
                       'ResultFileUrl': resultfile.ResultFileUrl,
                       'ResultFileType': resultfile.ResultFileType,
                       'CreateTime': resultfile.CreateTime.strftime("%Y-%m-%d-%H"),
                       'LastModifyTime': resultfile.LastModifyTime.strftime("%Y-%m-%d-%H")}
    return resultfile_dict


@csrf_exempt
def searchResultFile(request):
    if request.method == 'POST':
        dataname = request.POST['dataname']
        if dataname == '':
            data_list = list(ResultFile.objects.all())
        else:
            data_list = list(ResultFile.objects.filter(ResultFileName__contains=dataname))
        data_dict_list = []
        for i in data_list:
            data_dict_list.append(resultfile_to_dict(i))
        data = json.dumps(data_dict_list)
    return HttpResponse(data)


@csrf_exempt
def VisualizationDrawInfoShow(request):
    if request.method == 'POST':
        if request.POST['db_method'] == 'db_delete':
            nid = request.POST['db_id']
            try:
                VisualizationDrawInfo.objects.filter(VisualizationDrawID=nid).delete()
            except models.ProtectedError:
                return render(request, "cloudPlatform/666.html")
        if request.POST['db_method'] == 'db_add':
            new_DrawName = request.POST['new_DrawName']
            new_DrawDescrip = request.POST['new_DrawDescrip']
            if new_DrawName != '':
                VisualizationDrawInfo.objects.create(VisualizationDrawName=new_DrawName, VisualizationDrawDescrip=new_DrawDescrip)
            else:
                VisualizationDrawInfo.error(request, 'not valid input', extra_tags='bg-warning text-warning')
    data_list = VisualizationDrawInfo.objects.all()
    return render(request, 'cloudPlatform/VisualizationDrawInfoShow.html', {'data_list': data_list})


@csrf_exempt
def VisualizationDrawShow(request):
    '''
    read figure info from database and draw figures
    '''
    global visualization_draw_id_cache
    if request.method == 'POST':
        visualization_draw_id = request.POST['drawid']
        visualization_draw_id_cache = visualization_draw_id
    else:
        visualization_draw_id = visualization_draw_id_cache
    figure_list = VisualizationDraw.objects.filter(VisualizationDrawID__VisualizationDrawID=visualization_draw_id).order_by("VisualizationFigureID")
    typelist = TagType.objects.values('TagTypeName')
    tooltypelist = VisualizationToolType.objects.exclude(VisualizationToolTypeName='virtual tool type').values('VisualizationToolTypeName')
    context = {
        'page_name': 'VisualizationDrawShow',
        'figure_list': figure_list,
        'visualization_id': visualization_draw_id,
        'typelist': typelist,
        'tooltypelist': tooltypelist
    }
    return render(request, 'cloudPlatform/VisualizationDrawShow.html', context)

@csrf_exempt
def save_figure(request):
    '''
    update figure info in database
    '''
    if request.method == 'POST':
        visualization_id = request.POST['visualization_id']
        figure_list = json.loads(request.POST['figure_list'])
        VisualizationDraw.objects.filter(VisualizationDrawID__VisualizationDrawID=visualization_id).delete()
        r = VisualizationDrawInfo.objects.get(VisualizationDrawID=visualization_id)
        for i in figure_list:
            figure_id = i['figure_id']
            tool = VisualizationTool.objects.get(VisualizationToolName=i['tool'])
            data = ResultFile.objects.get(ResultFileID=i['data'])
            VisualizationDraw.objects.create(VisualizationDrawID=r, VisualizationFigureID=figure_id,
                                             VisualizationTool_VisualizationToolID=tool,
                                             ResultFile_ResultID=data)
        return HttpResponse(1)


@csrf_exempt
def ResultFileShow(request):
    if request.method == 'POST':
        if request.POST['db_method'] == 'db_delete':
            nid = request.POST['new_ResultFileID']
            resultType = ResultFile.objects.get(ResultFileID=nid).ResultFileType
            if resultType == 'direct':
                resultUrl = ResultFile.objects.get(ResultFileID=nid).ResultFileUrl
                postfix = resultUrl.split('//')[1]
                postfix = postfix[postfix.find('/')+1:]
                key = postfix[postfix.find('/')+1:]
                bucket = qingstor.Bucket(bucketName, zoneName)
                output = bucket.delete_object(key)
                ResultFile.objects.filter(ResultFileID=nid).delete()
            else:
                ResultFile.objects.filter(ResultFileID=nid).delete()

        if request.POST['db_method'] == 'db_modify':
            nid = request.POST['upload_resultFileID']
            upload_ResultFileName = request.POST['upload_resultFileName']
            if upload_ResultFileName == '':
                upload_ResultFileName = nid.replace('_','.')
            ResultFile.objects.filter(ResultFileID=nid).update(ResultFileName=upload_ResultFileName)

        if request.POST['db_method'] == 'db_add':
            new_ResultFileID = request.POST['upload_resultFileID']
            upload_fileDir = request.POST['upload_fileDir']
            upload_ResultFileName = request.POST['upload_resultFileName']
            success = True

            if new_ResultFileID != '':
                id = new_ResultFileID
                url = qingstorURL + '/' + id + '.json'
                file_type = 'calculation'
            elif upload_fileDir != '':
                id = upload_fileDir
                url = qingstorURL + '/' + upload_fileDir
                file_type = 'direct'
                if(request.POST['upload_result']!='success'):
                    success = False
            else:
                ResultFile.error(request, "输出错误！", extra_tags='bg-warning text-warning')

            if upload_ResultFileName == '':
                upload_ResultFileName = id
            id = id.replace('.','_')

            if ResultFile.objects.filter(ResultFileID=id).exists():
                ResultFile.error(request, '创建的文件已存在！', extra_tags='bg-warning text-warning')
            else:
                if success:
                    ResultFile.objects.create(ResultFileID=id, ResultFileName=upload_ResultFileName,
                                              ResultFileUrl=url, ResultFileType=file_type)

    data_list = ResultFile.objects.all()
    return render(request, 'cloudPlatform/ResultFileShow.html', {'data_list': data_list})


@csrf_exempt
def AnalysisToolExecutor(request):
    typelist = TagType.objects.values('TagTypeName')
    tooltypelist = AnalysisToolType.objects.exclude(AnalysisToolTypeName='virtual tool type').values('AnalysisToolTypeName')
    return render(request, 'cloudPlatform/AnalysisToolExecutor.html', {'page_name': 'demandmodule', 'typelist': typelist, 'tooltypelist': tooltypelist})




def getExecutor(id, pid, para):
    return 'cloudPlatform/executor/daemon.sh ' + str(id) + ' ' + str(pid) + ' ' + str(para)


jobInfo = {}

def ajaxGetLogHandle(request):
    if request.method == 'GET':
        jobID = request.GET['jobID']
        pid = jobInfo[jobID]['pid']
        line = jobInfo[jobID]['line']
        executor = getExecutor(jobID, pid, line)
        job = subprocess.Popen(executor, stdout=subprocess.PIPE, shell=True)
        job.wait()
        result = job.stdout.read()
        if result != '' or job.returncode == 1:
            count = result.count('\n')
            jobInfo[jobID]['line'] += count
            return HttpResponse(result)
        else:
            return HttpResponse(500)

    else:
        return HttpResponse(-1)

def ajaxGetLog(request):
    if request.method == 'GET':
        toolName = request.GET['toolName']
        inputFile = request.GET['inputFile']
        platform = request.GET['platform']
        jobID = str(112311)
        if platform == 'anaconda':
            command = '"RPCClient/client.py ' + toolName + ' ' + inputFile +'"'
        elif platform == 'hadoop':
            command = '"'+'/usr/local/hadoop/bin/hadoop jar hadoop/hadoopTools-1.0.0.jar Main ' + toolName + '"'
        executor = getExecutor(jobID, 0, command)
        job = subprocess.Popen(executor, stdout=subprocess.PIPE, shell=True)
        if job.wait() == 1:
            pid = job.stdout.read().replace('\n', '')
            jobInfo[jobID] = {}
            jobInfo[jobID]['pid'] = pid
            jobInfo[jobID]['line'] = 1
            return HttpResponse(jobID)
        else:
            return HttpResponse(-1)
    else:
        return HttpResponse(-1)
