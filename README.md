#AndroidCloud [![Build Status](https://travis-ci.org/twlkyao/AndroidCloud.png?branch=master)](https://travis-ci.org/twlkyao/AndroidCloud)

An Android Application to synchronize your data to the cloud with some extra secure measures.

##Direction

1. Register/Login.  
2. Find the file you want to deal with(encrypt/decrypt).  
3. Click the file, choose the encrypt level you want, then the file will be encrypted.  
4. Upload your file to the cloud(Call the third-party api).    
5. Download your file from the cloud(Call the third-party api).  
6. Long click the file, then the file will be decrypted, if the file is not broken.  
7. When you download an apk file, the file will be checked whether it is authorized or not.  
8. If the apk is not authorized, it will be deleted, or it is OK to install.  

##Architecture

1. The device side has part of the key.
2. The server side randomly generate part of the key.
3. Set file important level and encrypts the file when uploads.
4. Pass the key in ciphpertext(Use HTTPS or other methods).
5. Validate the integrity of the stored data.
6. Validate the safety of the downloaded Apps.  
7. Encrypt the contacts and messages.  
8. Recover the contacts and messages.

##Attention  

Part 4 and part 5 of Directions wil use a third party application, in order to be safe.  

##TODO list

<del>1. The Apps validation on the Android side.</del><br>  
  
<del>2. Fix the FileObserver's jump condition(When to start another Activity, solved by using Notification).</del><br>

<del>3. The device side has part of the key.</del><br>  

<del>4. Add more implements of Encryption/Decryption.</del><br>  

<del>5. Implement the same function for contacts and message parts.</del><br>

<del>6. Implement the key splice.</del><br>

7. Implement the same function for contacts.  

8. Encrypt the voice communication data.  

9. Encrypt the SMS data.  

10. Integrate the third party cloud APIs(Kuaipan is integrated).  

11. Key management.

12. Optimize the menu display.

13. Move the functions to listview's onItemClickListener.
