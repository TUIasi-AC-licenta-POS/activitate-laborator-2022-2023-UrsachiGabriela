from spyne import Application, rpc, ServiceBase, Integer, Double
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication



class RealCalculatorService(ServiceBase):
    @rpc(Double, Double, _returns=Double)
    def addition(ctx, x, y):
        return x + y

    @rpc(Double, Double, _returns=Double)
    def substraction(ctx, a, b):
        return a - b

    @rpc(Double, Double, _returns=Double)
    def multiplication(ctx, a, b):
        return a * b

    @rpc(Double, Double, _returns=Double)
    def division(ctx, a, b):
        return a / b


application = Application([RealCalculatorService], 'services.realcalculator.soap',
                          in_protocol=Soap11(validator='lxml'),
                          out_protocol=Soap11())

wsgi_application = WsgiApplication(application)

if __name__ == '__main__':
    import logging

    from wsgiref.simple_server import make_server

    logging.basicConfig(level=logging.INFO)
    logging.getLogger('spyne.protocol.xml').setLevel(logging.INFO)

    logging.info("listening to http://127.0.0.1:8000")
    logging.info("wsdl is at: http://127.0.0.1:8000/?wsdl")

    server = make_server('127.0.0.1', 8000, wsgi_application)
    server.serve_forever()
