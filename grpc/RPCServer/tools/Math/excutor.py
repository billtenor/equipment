from common.iostream import IOStream
import Math_pb2_grpc,Math_pb2
from numpy import sum
from numpy import ndarray
from RPCServer.tools.BaseTool import BaseTool

def addToGrpc(grpcServer):
    Math_pb2_grpc.add_MathServicer_to_server(Math(), grpcServer)


class Math(Math_pb2_grpc.MathServicer, BaseTool):

    def checkParameter(self, data):
        return True

    def checkDataType(self, data):
        if type(data) == ndarray:
            return True
        else:
            return False

    def sum(self, request, context):
        iostream = IOStream.initFromBytes(request.bytes)
        if self.checkParameter(iostream.parameter) and self.checkDataType(iostream.data):
            result = IOStream(parameter=None, data=sum(iostream.data))
            return Math_pb2.SerializedIOStream(bytes=iostream.toBytes(result))
        else:
            return Math_pb2.SerializedIOStream(bytes=iostream.toBytes("error input!"))
