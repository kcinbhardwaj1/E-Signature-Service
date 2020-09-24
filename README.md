E-Signature API

Folder content:
============================
1- E-Signature-Service-0.0.1-SNAPSHOT.jar
2- application.properties
3- application-test.properties

Installation And Prerequisites:
===============================
A DocuSign Developer Sandbox account (email and password) on [demo.docusign.net](https://demo.docusign.net).
1. A DocuSign Integration Key (a client ID) and PRIVATE_KEY that is configured to use the
   JWT Code flow.
2. Store private key in environment variable by DS_PRIVATE_KEY name
3. Java 8.
4. Maven 3.6.2

GETTING STARTED
============================
- This folder contains a service that communicate with DocuSign service

- This service exposes a few RESTful endpoints to manipulate a envelope. The service exposes a swagger definition to these endpoints. They can be viewed locally after you run the service here: http://localhost:7444/swagger-ui.html#!

- you can access hystrix dashboard by following URL  
http://localhost:7444/hystrix and the streams are monitored with following URL https://localhost:7444/actuator/hystrix.stream 

- The service is already configured to run locally on port 7444 and it is connected to an account on DocuSign through JWT authorization. (details are in the application.properties)

- Created a DocuSign Sandbox account please use that to make RESTful calls.

RUNNING THE SERVICE
============================
- We are using java 1.8
- open a command prompt to the location of this folder and run the following command
 > java -jar E-Signature-Service-0.0.1-SNAPSHOT.jar --spring.config.location=application.properties
 
- Following are the two flows for signing
	1. Remote(Signing done envelope sent via email)
	2. Embed (Signing done within application)
- For this we have following four endpoints:
	1. Remote Signing using template
	2. Remote Signing without using template
	3. Embed Signing using template
	4. Embed Signing without using template
 
create and send your envelope for remote signing without or with template use following URL respectively http://localhost:7444/envelope and http://localhost:7444/envelope/template    POST 
If envelope is created succussefully you get following response in a json.
 {
    "envelopeId": "588f14a6-c70e-4443-babd-37a0dbcc3002",
    "status": "sent",
    "statusDateTime": "2020-06-23T06:55:42.2160863Z",
    "uri": "/envelopes/588f14a6-c70e-4443-babd-37a0dbcc3002"
 } 
 
create and send your envelope for embed signing without or with template use following URL respectively http://localhost:7444/envelope/embed and http://localhost:7444/envelope/embed/template    POST 
If envelope is created succussefully you get following response in a json.
 {
    "envelopeId": "588f14a6-c70e-4443-babd-37a0dbcc3002",            
    "embeds":   [
					{
                        "signerName": "swati",
                        "viewUrl": "https://demo.docusign.net/Signing/MTRedeem/v1/0679dbcf-ecbf-4a8e-b458-f6311b9fc58b?slt=eyJ0eXAiOiJNVCIsImFsZyI6IlJTMjU2Iiwia2lkIjoiNjgxODVmZjEtNGU1MS00Y2U5LWFmMWMtNjg5ODEyMjAzMzE3In0.AQUAAAABAAMABwAAYyYnQxfYSAgAAMH22UMX2EgYAAEAAAAAAAAAIQBdAwAAeyJUb2tlbklkIjoiYzMyZjkwZDAtOGYyMy00MzZhLWFlNTctN2Q1MDgxZGRkZjAxIiwiU3ViamVjdCI6bnVsbCwiU3ViamVjdFR5cGUiOm51bGwsIkV4cGlyYXRpb24iOiIyMDIwLTA2LTIzVDA3OjA1OjQ2KzAwOjAwIiwiSXNzdWVkQXQiOiIyMDIwLTA2LTIzVDA3OjAwOjQ2LjE3MjY2NzUrMDA6MDAiLCJSZXNvdXJjZUlkIjoiYzk1OTYxOWMtOWU2NS00YWZmLWI1OWMtNjgzYWE5OTQ0OGYxIiwiTGFiZWwiOm51bGwsIlNpdGVJZCI6bnVsbCwiUmVzb3VyY2VzIjoie1wiRW52ZWxvcGVJZFwiOlwiYzk1OTYxOWMtOWU2NS00YWZmLWI1OWMtNjgzYWE5OTQ0OGYxXCIsXCJBY3RvclVzZXJJZFwiOlwiOTZkN2IwMWItODg3Ny00NzllLWI1ZjktZTcwNTgxOTAyZmE1XCIsXCJSZWNpcGllbnRJZFwiOlwiOGZjMDdlZTYtNWVkNS00YmJjLTg3ODktY2QwZDFiMjBjMzZhXCIsXCJGYWtlUXVlcnlTdHJpbmdcIjpcInQ9ZWY0YjhlMzItZWNmNC00YjBhLTg2OTEtMmEyNzgwMDI0ZmUwXCJ9IiwiT0F1dGhTdGF0ZSI6bnVsbCwiVG9rZW5UeXBlIjoxLCJBdWRpZW5jZSI6IjI1ZTA5Mzk4LTAzNDQtNDkwYy04ZTUzLTNhYjJjYTU2MjdiZiIsIlNjb3BlcyI6bnVsbCwiUmVkaXJlY3RVcmkiOiJodHRwczovL2RlbW8uZG9jdXNpZ24ubmV0L1NpZ25pbmcvU3RhcnRJblNlc3Npb24uYXNweCIsIkhhc2hBbGdvcml0aG0iOjAsIkhhc2hTYWx0IjpudWxsLCJIYXNoUm91bmRzIjowLCJUb2tlblNlY3JldEhhc2giOm51bGwsIlRva2VuU3RhdHVzIjowLCJFeHRlcm5hbENsYWltc1JlcXVlc3RlZCI6bnVsbCwiVHJhbnNhY3Rpb25JZCI6bnVsbCwiVHJhbnNhY3Rpb25FdmVudENhbGxiYWNrVXJsIjpudWxsLCJJc1NpbmdsZVVzZSI6ZmFsc2V9.aCQCfUD6insRc8-RnFUhTopCuxr0e4sAp93pM1RsDtQDQK4Wv7BGrxz-Vqkb2rF_9tKoGfizgI-mlIDIefCtIlFK01Y7XrUAleACoM8uZHMdyZr7OZlycORPCvIFLlqLxfcQDN3cBH4SAdGe0Z6N0LnYLWqvvfTXv-Gf7ulCgZ1SqouL8L7hfRFIHRrpWwGW18rk_kpwc96dXO9q_QvPy060556j6fxSMN0Xee_yg9FUsJ3XtoJacl_roj_FykoFrFNWvs7juDcjPBMAZTwgzRRniNXdyxf3LDHvnW0srmFxAPGHRvpbL08mnLq7tbkv6ZXuL4PNqq8ei5olL7yjRQ"
                    },
                    {
                        "signerName": "khem",
                        "viewUrl": "https://demo.docusign.net/Signing/MTRedeem/v1/0679dbcf-ecbf-4a8e-b458-f6311b9fc58b?slt=eyJ0eXAiOiJNVCIsImFsZyI6IlJTMjU2Iiwia2lkIjoiNjgxODVmZjEtNGU1MS00Y2U5LWFmMWMtNjg5ODEyMjAzMzE3In0.AQUAAAABAAMABwAAYyYnQxfYSAgAAMH22UMX2EgYAAEAAAAAAAAAIQBdAwAAeyJUb2tlbklkIjoiYzMyZjkwZDAtOGYyMy00MzZhLWFlNTctN2Q1MDgxZGRkZjAxIiwiU3ViamVjdCI6bnVsbCwiU3ViamVjdFR5cGUiOm51bGwsIkV4cGlyYXRpb24iOiIyMDIwLTA2LTIzVDA3OjA1OjQ2KzAwOjAwIiwiSXNzdWVkQXQiOiIyMDIwLTA2LTIzVDA3OjAwOjQ2LjE3MjY2NzUrMDA6MDAiLCJSZXNvdXJjZUlkIjoiYzk1OTYxOWMtOWU2NS00YWZmLWI1OWMtNjgzYWE5OTQ0OGYxIiwiTGFiZWwiOm51bGwsIlNpdGVJZCI6bnVsbCwiUmVzb3VyY2VzIjoie1wiRW52ZWxvcGVJZFwiOlwiYzk1OTYxOWMtOWU2NS00YWZmLWI1OWMtNjgzYWE5OTQ0OGYxXCIsXCJBY3RvclVzZXJJZFwiOlwiOTZkN2IwMWItODg3Ny00NzllLWI1ZjktZTcwNTgxOTAyZmE1XCIsXCJSZWNpcGllbnRJZFwiOlwiOGZjMDdlZTYtNWVkNS00YmJjLTg3ODktY2QwZDFiMjBjMzZhXCIsXCJGYWtlUXVlcnlTdHJpbmdcIjpcInQ9ZWY0YjhlMzItZWNmNC00YjBhLTg2OTEtMmEyNzgwMDI0ZmUwXCJ9IiwiT0F1dGhTdGF0ZSI6bnVsbCwiVG9rZW5UeXBlIjoxLCJBdWRpZW5jZSI6IjI1ZTA5Mzk4LTAzNDQtNDkwYy04ZTUzLTNhYjJjYTU2MjdiZiIsIlNjb3BlcyI6bnVsbCwiUmVkaXJlY3RVcmkiOiJodHRwczovL2RlbW8uZG9jdXNpZ24ubmV0L1NpZ25pbmcvU3RhcnRJblNlc3Npb24uYXNweCIsIkhhc2hBbGdvcml0aG0iOjAsIkhhc2hTYWx0IjpudWxsLCJIYXNoUm91bmRzIjowLCJUb2tlblNlY3JldEhhc2giOm51bGwsIlRva2VuU3RhdHVzIjowLCJFeHRlcm5hbENsYWltc1JlcXVlc3RlZCI6bnVsbCwiVHJhbnNhY3Rpb25JZCI6bnVsbCwiVHJhbnNhY3Rpb25FdmVudENhbGxiYWNrVXJsIjpudWxsLCJJc1NpbmdsZVVzZSI6ZmFsc2V9.aCQCfUD6insRc8-RnFUhTopCuxr0e4sAp93pM1RsDtQDQK4Wv7BGrxz-Vqkb2rF_9tKoGfizgI-mlIDIefCtIlFK01Y7XrUAleACoM8uZHMdyZr7OZlycORPCvIFLlqLxfcQDN3cBH4SAdGe0Z6N0LnYLWqvvfTXv-Gf7ulCgZ1SqouL8L7hfRFIHRrpWwGW18rk_kpwc96dXO9q_QvPy060556j6fxSMN0Xee_yg9FUsJ3XtoJacl_roj_FykoFrFNWvs7juDcjPBMAZTwgzRRniNXdyxf3LDHvnW0srmFxAPGHRvpbL08mnLq7tbkv6ZXuL4PNqq8ei5olL7yjRQ"
                    }]
 }

- http://localhost:7444/envelope/{days} GET
This will return list of envelopes of specified days with all the details of recipients and document

- http://localhost:7444/envelope/{envelopeId}/document GET
This will return list of base64 encoded documents present inside envelope

- http://localhost:7444/envelope/{envelopeId}/document/{docId} GET
This will return base64 encoded document present inside envelope

- http://localhost:7444/envelope/{envelopeId}/document/{docId} DELETE
This will delete document present inside envelope

- http://localhost:7444/envelope/query POST
This will return filtered list of envelopes with all the details of recipients and document

- http://localhost:7444/envelope/status POST
webhook listener endpoint which accept xml file as string and parse that xml and stores envelope status information in in-memory database.

- http://localhost:7444/envelope/status/{envelopeId} GET
This will return envelope object which contains envelope and recipients status

- http://localhost:7444/template GET
This will return list of templates
