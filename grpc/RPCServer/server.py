import grpc
import time
import json
import os
import sys
from concurrent import futures
sys.path.append(os.path.join(os.path.dirname(sys.argv[0]),os.path.pardir))
_ONE_DAY_IN_SECONDS = 60 * 60 * 24


def main():
    thisDir = os.path.split(os.path.realpath(__file__))[0]
    myFile = open(thisDir+'/config.json', 'r')
    try:
        fileContext = myFile.read()
    finally:
        myFile.close()

    config = json.loads(fileContext)
    grpcServer = grpc.server(futures.ThreadPoolExecutor(max_workers=config["max_workers"]))
    rootDir = thisDir + "/tools"
    for list in os.listdir(rootDir):
        path = os.path.join(rootDir, list)
        if os.path.isdir(path):
            sys.path.append(path)
            model = __import__("excutor")
            addToGrpc = getattr(model, "addToGrpc")
            addToGrpc(grpcServer)

    grpcServer.add_insecure_port(config["host"] + ':' + config["port"])
    grpcServer.start()

    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        grpcServer.stop(0)

if __name__ == '__main__':
    main()
