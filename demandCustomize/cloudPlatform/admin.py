from django.contrib import admin
from cloudPlatform import models
# Register your models here.
admin.site.register(models.TagType)
admin.site.register(models.Tag)
admin.site.register(models.AnalysisToolType)
admin.site.register(models.AnalysisTool)
admin.site.register(models.AnalysisTool_has_Tag)
admin.site.register(models.DemandType)
admin.site.register(models.Demand)
admin.site.register(models.Demand_has_Tag)
admin.site.register(models.DemandTemplate)
admin.site.register(models.DemandTemplateNode)
admin.site.register(models.AnalysisToolConnect)
admin.site.register(models.VisualizationToolType)
admin.site.register(models.VisualizationTool)
admin.site.register(models.VisualizationTool_has_Tag)
admin.site.register(models.ResultFile)
admin.site.register(models.VisualizationDraw)
