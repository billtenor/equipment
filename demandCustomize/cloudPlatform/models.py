from __future__ import unicode_literals

from django.db import models

# Create your models here.
name_length = 45
description_length = 100
parameter_length = 500
version_length = 10
enum_length = 15
hasID_length = 35
fileName_length = 45


class TagType(models.Model):
    TagTypeID = models.AutoField(primary_key=True)
    TagTypeName = models.CharField(max_length=name_length)
    TagTypeDescrip = models.CharField(max_length=description_length, blank=True)


class Tag(models.Model):
    TagID = models.AutoField(primary_key=True)
    TagLabel = models.CharField(max_length=name_length)
    TagDescrip = models.CharField(max_length=description_length, blank=True)
    CreateTime = models.DateTimeField(auto_now_add=True)
    LastModifyTime = models.DateTimeField(auto_now=True)
    TagType_TagTypeID = models.ForeignKey(TagType, on_delete=models.PROTECT)


class AnalysisToolType(models.Model):
    AnalysisToolTypeID = models.AutoField(primary_key=True)
    AnalysisToolTypeName = models.CharField(max_length=name_length)
    AnalysisToolTypeDescrip = models.CharField(max_length=description_length, blank=True)


class AnalysisTool(models.Model):
    AnalysisToolID = models.AutoField(primary_key=True)
    AnalysisToolName = models.CharField(max_length=name_length)
    AnalysisToolDescrip = models.CharField(max_length=description_length, blank=True)
    AnalysisToolPlatform = models.CharField(max_length=enum_length)
    AnalysisToolVersion = models.CharField(max_length=version_length)
    CreateTime = models.DateTimeField(auto_now_add=True)
    LastModifyTime = models.DateTimeField(auto_now=True)
    AnalysisToolType_AnalysisToolTypeID = models.ForeignKey(AnalysisToolType, on_delete=models.PROTECT)


class AnalysisTool_has_Tag(models.Model):
    AnalysisToolID_TagID = models.CharField(max_length=hasID_length, primary_key=True)
    AnalysisTool_AnalysisToolID = models.ForeignKey(AnalysisTool, on_delete=models.CASCADE)
    Tag_TagID = models.ForeignKey(Tag, on_delete=models.PROTECT)
    def save(self, *args, **kwargs):
        _AnalysisToolID = str(self.AnalysisTool_AnalysisToolID.AnalysisToolID)
        _TagID = str(self.Tag_TagID.TagID)
        self.AnalysisToolID_TagID = _AnalysisToolID + ':' + _TagID
        super(AnalysisTool_has_Tag, self).save(*args, **kwargs)


class DemandType(models.Model):
    DemandTypeID = models.AutoField(primary_key=True)
    DemandTypeName = models.CharField(max_length=name_length)
    DemandTypeDescrip = models.CharField(max_length=description_length, blank=True)


class Demand(models.Model):
    DemandID = models.AutoField(primary_key=True)
    DemandName = models.CharField(max_length=name_length)
    DemandDescrip = models.CharField(max_length=description_length, blank=True)
    CreateTime = models.DateTimeField(auto_now_add=True)
    LastModifyTime = models.DateTimeField(auto_now=True)
    DemandType_DemandTypeID = models.ForeignKey(DemandType, on_delete=models.PROTECT)


class Demand_has_Tag(models.Model):
    DemandID_TagID = models.CharField(max_length=hasID_length, primary_key=True)
    Demand_DemandID = models.ForeignKey(Demand, on_delete=models.PROTECT)
    Tag_TagID = models.ForeignKey(Tag, on_delete=models.PROTECT)
    def save(self, *args, **kwargs):
        _DemandID = str(self.Demand_DemandID.DemandID)
        _TagID = str(self.Tag_TagID.TagID)
        self.DemandID_TagID = _DemandID + ':' + _TagID
        super(Demand_has_Tag, self).save(*args, **kwargs)


class DemandTemplate(models.Model):
    DemandTemplateID = models.AutoField(primary_key=True)
    DemandTemplateName = models.CharField(max_length=name_length)
    DemandTemplateDescrip = models.CharField(max_length=description_length, blank=True)
    StartMethodNode = models.IntegerField(default=0)
    CreateTime = models.DateTimeField(auto_now_add=True)
    LastModifyTime = models.DateTimeField(auto_now=True)
    DemandTemplatePara = models.CharField(max_length=parameter_length)
    Demand_DemandID = models.ForeignKey(Demand, on_delete=models.PROTECT)


class DemandTemplateNode(models.Model):
    DemandTemplateNodeID = models.AutoField(primary_key=True)
    NodeType = models.CharField(max_length=enum_length)
    DemandTemplate_DemandTemplateID = models.ForeignKey(DemandTemplate, on_delete=models.CASCADE)
    AnalysisTool_AnalysisToolID = models.ForeignKey(AnalysisTool, on_delete=models.PROTECT)
    AnalysisToolPara = models.CharField(max_length=parameter_length, blank=True)


class AnalysisToolConnect(models.Model):
    DemandTemplateID_FrontID_BackID = models.CharField(max_length=hasID_length, primary_key=True)
    FrontNodeID = models.ForeignKey(DemandTemplateNode, related_name='FrontDemandTemplateNode', on_delete=models.CASCADE)
    BackNodeID = models.ForeignKey(DemandTemplateNode, related_name='BackDemandTemplateNode', on_delete=models.CASCADE)
    DemandTemplate_DemandTemplateID = models.ForeignKey(DemandTemplate, on_delete=models.CASCADE)
    DataDescrip = models.CharField(max_length=parameter_length)
    def save(self, *args, **kwargs):
        _FrontNodeID = str(self.FrontNodeID.DemandTemplateNodeID)
        _BackNodeID = str(self.BackNodeID.DemandTemplateNodeID)
        _DemandTemplateID = str(self.DemandTemplate_DemandTemplateID.DemandTemplateID)
        self.DemandTemplateID_FrontID_BackID = _DemandTemplateID + ':' + _FrontNodeID + ':' + _BackNodeID
        super(AnalysisToolConnect, self).save(*args, **kwargs)


class VisualizationToolType(models.Model):
    VisualizationToolTypeID = models.AutoField(primary_key=True)
    VisualizationToolTypeName = models.CharField(max_length=name_length)
    VisualizationToolTypeDescrip = models.CharField(max_length=description_length)


class VisualizationTool(models.Model):
    VisualizationToolID = models.AutoField(primary_key=True)
    VisualizationToolName = models.CharField(max_length=name_length)
    VisualizationToolDescrip = models.CharField(max_length=description_length)
    VisualizationToolVersion = models.CharField(max_length=version_length)
    CreateTime = models.DateTimeField(auto_now_add=True)
    LastModifyTime = models.DateTimeField(auto_now=True)
    VisualizationToolType_VisualizationToolID = models.ForeignKey(VisualizationToolType, on_delete=models.PROTECT)


class VisualizationTool_has_Tag(models.Model):
    VisualizationToolID_TagID = models.CharField(max_length=hasID_length, primary_key=True)
    VisualizationTool_VisualizationToolID = models.ForeignKey(VisualizationTool, on_delete=models.PROTECT)
    Tag_TagID = models.ForeignKey(Tag, on_delete=models.PROTECT)
    def save(self, *args, **kwargs):
        _VisualizationToolID = str(self.VisualizationTool_VisualizationToolID.VisualizationToolID)
        _TagID = str(self.Tag_TagID.TagID)
        self.VisualizationToolID_TagID = _VisualizationToolID + ':' + _TagID
        super(VisualizationTool_has_Tag, self).save(*args, **kwargs)


class ResultFile(models.Model):
    ResultFileID = models.CharField(primary_key=True,max_length=fileName_length)
    ResultFileName = models.CharField(max_length=name_length)
    ResultFileUrl = models.CharField(max_length=description_length)
    ResultFileType = models.CharField(max_length=enum_length)
    CreateTime = models.DateTimeField(auto_now_add=True)
    LastModifyTime = models.DateTimeField(auto_now=True)


class VisualizationDrawInfo(models.Model):
    VisualizationDrawID = models.AutoField(primary_key=True)
    VisualizationDrawName = models.CharField(max_length=name_length)
    VisualizationDrawDescrip = models.CharField(max_length=description_length, blank=True)


class VisualizationDraw(models.Model):
    VisualizationDraw_RecordID = models.AutoField(primary_key=True)
    VisualizationDrawID = models.ForeignKey(VisualizationDrawInfo, on_delete=models.CASCADE)
    VisualizationFigureID = models.IntegerField()
    VisualizationTool_VisualizationToolID = models.ForeignKey(VisualizationTool, on_delete=models.PROTECT)
    ResultFile_ResultID = models.ForeignKey(ResultFile, on_delete=models.PROTECT)

