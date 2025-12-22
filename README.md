Date: 22/12/2025
Till now, I am able to send large file over wifi direct from client to server but it is not stable.
i am getting following error at client side:
========================
12-22 21:24:18.318 31350  9311 D AWARE_ASM: MyCustomForm: Toast shown -> sending file....
12-22 21:24:18.349 31350  9311 E AWARE_ASM: Socket IO error
12-22 21:24:18.349 31350  9311 E AWARE_ASM: java.net.SocketException: Software caused connection abort
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at java.net.SocketOutputStream.socketWrite0(Native Method)
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at java.net.SocketOutputStream.socketWrite(SocketOutputStream.java:116)
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at java.net.SocketOutputStream.write(SocketOutputStream.java:156)
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at java.io.DataOutputStream.write(DataOutputStream.java:107)
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at java.io.DataOutputStream.writeUTF(DataOutputStream.java:401)
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at java.io.DataOutputStream.writeUTF(DataOutputStream.java:323)
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at com.wifidirect.app.MainActivity.sendFile(MainActivity.java:337)
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at com.wifidirect.app.MainActivity.SendFileToServer(MainActivity.java:273)
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at
com.wifidirect.app.MyCustomForm.lambda$AddSendButton$5$com-wifidirect-app-MyCustomForm(MyCustomForm.java:161)
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at com.wifidirect.app.MyCustomForm$$ExternalSyntheticLambda1.run(Unknown Source:4)
12-22 21:24:18.349 31350  9311 E AWARE_ASM:     at java.lang.Thread.run(Thread.java:1119)
12-22 21:24:21.422 31350 31350 D AWARE_ASM: Calling requestPeers
========================
