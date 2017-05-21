#!/usr/bin/python2.7
import grpc
from common.iostream import IOStream
from tools import Math_pb2, Math_pb2_grpc
from numpy import array
import sys
from qingstor.sdk.service.qingstor import QingStor
from qingstor.sdk.config import Config
import tempfile
import json

config = Config('IKLQINFIKLQKWRMCJVLN', 'HcTZxhXDTSktGKsDnIH9nGTprs1XIJUuSaM9Cu1x')
qingstor = QingStor(config)
bucketName = 'equipment'
zoneName = 'pek3a'
folder = 'analysisTool/'

_HOST = '166.111.180.20'
_PORT = '8787'

def main(argv):
    toolName = argv[1]
    inputFile = argv[2]
    conn = grpc.insecure_channel(_HOST + ':' + _PORT)
    client = Math_pb2_grpc.MathStub(channel=conn)
    bucket = qingstor.Bucket(bucketName, zoneName)
    resp = bucket.get_object(folder+inputFile)
    with tempfile.TemporaryFile() as f:
        for chunk in resp.iter_content():
            f.write(chunk)
        f.seek(0)
        data = json.loads(f.read())
        f.close()
    if toolName == 'Math.sum':
        myArray = array(data['data'][0])
        input = IOStream(data=myArray)
        response = client.sum(Math_pb2.SerializedIOStream(bytes=IOStream.toBytes(input)))
        output = IOStream.initFromBytes(response.bytes)
        print output.data

if __name__ == '__main__':
    main(sys.argv)
