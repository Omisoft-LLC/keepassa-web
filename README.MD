README
Backend Server
-- Algorithums
1. Generate PKI. - return two crypto key objects 
2. Wrap PrivateKey/PublicKey in Password Based AES (SHA256) key - return bytearray
3. convert byte array to string - return string  
3.1. wrap public/private with AES from user password  
3.2. store AES in sessionStorage
4. Store string (Public/PrivateKey) in local storage
5. Export PublicKey in jwk
6. Convert jwk to String
7. Send publickey as string to Server


-- Encrypt password/file
1. Generate AES key, 
2. Encrypt password/file with AES key
3. Wrap AES key in Public Key - return  bytearray
4. Encode bytearray as base64
5. Send Password(char array) and Wraped AES key (base64)
6. Store AES key as base64 string  
  
-- Decrypt password/file 
1. Get wrapped AES key(String) and password
2. Convert AES key from base64, to bytearray
3. Unwrap AES key with Client PrivateKey
4. Decrypt password/file with AES key

-- Share password
Same as encrypt (we create copy of passwordsafe),
but we use all users public keys to wrap AES key
