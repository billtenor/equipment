install

python -m pip install grpcio
python -m pip install grpcio-tools

python -m grpc_tools.protoc -I ../../protos/ --python_out=. --grpc_python_out=. ../../protos/Math.proto
