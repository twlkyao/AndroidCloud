#AndroidCloud [![Build Status](https://travis-ci.org/twlkyao/AndroidCloud.png?branch=master)](https://travis-ci.org/twlkyao/AndroidCloud)

An Android Application to synchronize your data to the cloud with some extra secure measures.

##Direction

1. Register/Login.  
2. Find the file you want to deal with(encrypt/decrypt).  
3. Click the file, choose the encrypt level you want, then the file will be encrypted.  
4. Upload your file.  
5. Download your file from the cloud.  
6. Long click the file, then the file will be decrypted, if the file is not broken.  
7. When you download an apk file, the file will be checked whether it is authorized or not.  
8. If the apk is not authorized, it will be deleted, or it is OK to install.  

##Architecture

1. The device side has part of the key.
2. The server side randomly generate part of the key.
3. Set file important level and encrypts the file when uploads.(TODO)
4. Pass the key in ciphpertext.(TODO)
5. Validate the integrity of the stored data.
6. Validate the integritey of the downloaded Apps.  
7. Encrypt the contacts and messages.

##Attention  

Part 4 and part 5 of Directions wil use a third party application, in order to be safe.  

##TODO list

<del>1. The Apps validation on the Android side.</del><br>  
  
<del>2. Fix the FileObserver's jump condition(When to start another Activity, solved by using Notification).</del><br>

<del>3. The device side has part of the key.</del><br>  

4. Add more implements of Encryption.  

5. Implement the same function for contacts and message parts.  

6. Implement the key splice.  

7. Encrypt the communication data.  
