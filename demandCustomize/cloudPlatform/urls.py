from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^index', views.index, name='index'),
    url(r'^dag_before_test', views.dag_before_test, name='dag_before_test'),
    url(r'^demandmodule', views.demandmodule, name="demandmodule"),
    url(r'^searchTool', views.searchTool, name='searchTool'),
    url(r'^selectTagType', views.selectTagType, name='selectTagType'),
    url(r'^AnalysisToolTypeShow', views.AnalysisToolTypeShow, name='AnalysisToolTypeShow'),
    url(r'^TagTypeShow', views.TagTypeShow, name='TagTypeShow'),
    url(r'^TagShow', views.TagShow, name='TagShow'),
    url(r'^getTags', views.getTags, name='getTags'),
    url(r'^AnalysisToolShow', views.AnalysisToolShow, name='AnalysisToolShow'),
    url(r'^getDAG', views.getDAG, name='getDAG'),
    url(r'^saveDAG', views.saveDAG, name='saveDAG'),
    url(r'^demandtemplateShow', views.demandtemplateShow, name='demandtemplateShow'),
    url(r'^adddemandmodule', views.adddemandmodule, name='adddemandmodule'),
    url(r'^DemandTypeShow', views.DemandTypeShow, name='DemandTypeShow'),
    url(r'^DemandShow', views.DemandShow, name='DemandShow'),
    url(r'^VisToolTypeShow', views.VisToolTypeShow, name='VisToolTypeShow'),
    url(r'^VisToolShow', views.VisToolShow, name='VisToolShow'),
    url(r'^getTags', views.getTags, name='getTags'),
    url(r'^getAnalysisToolTags', views.getAnalysisToolTags, name='getAnalysisToolTags'),
    url(r'^getDemandTags', views.getDemandTags, name='getDemandTags'),
    url(r'^getVisualizationToolTags', views.getVisualizationToolTags, name='getVisualizationToolTags'),
    url(r'^searchVisualizationTool', views.searchVisualizationTool, name='searchVisualizationTool'),
    url(r'^searchResultFile', views.searchResultFile, name='searchResultFile'),
    url(r'^VisualizationDrawInfoShow', views.VisualizationDrawInfoShow, name = 'VisualizationDrawInfoShow'),
    url(r'^VisualizationDrawShow', views.VisualizationDrawShow, name = 'VisualizationDrawShow'),
    url(r'^save_figure', views.save_figure, name = 'saveFigure'),
    url(r'^ResultFileShow', views.ResultFileShow, name = 'ResultFileShow'),
    url(r'^AnalysisToolExecutor', views.AnalysisToolExecutor, name = 'AnalysisToolExecutor'),
    url(r'^ajaxGetLogHandle', views.ajaxGetLogHandle, name = 'ajaxGetLogHandle'),
    url(r'^ajaxGetLog', views.ajaxGetLog, name = 'ajaxGetLog'),
]