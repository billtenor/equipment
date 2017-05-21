import pickle


class IOStream():
    def __init__(self, parameter=None, data=None):
        self.parameter = parameter
        self.data = data

    @staticmethod
    def initFromBytes(bytes):
        return pickle.loads(bytes)

    @staticmethod
    def toBytes(iostream):
        return pickle.dumps(iostream)


