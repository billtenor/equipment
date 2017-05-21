from abc import ABCMeta,abstractmethod

class BaseTool(object):
    __metaclass__ = ABCMeta

    def __init__(self):
        self.headers = ''

    @abstractmethod
    def checkParameter(self):
        pass

    @abstractmethod
    def checkDataType(self):
        pass