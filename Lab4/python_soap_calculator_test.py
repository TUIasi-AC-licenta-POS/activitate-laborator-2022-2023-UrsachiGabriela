# python -m pip install suds
from suds.client import Client
c = Client('http://localhost:8000/?wsdl')
print(c.service.addition(10, 5))
print(c.service.substraction(10, 5))
