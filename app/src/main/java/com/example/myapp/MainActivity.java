package com.wifidirect.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import java.util.Collection;
import android.net.wifi.p2p.WifiP2pDevice;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;
import java.net.InetSocketAddress;
import android.content.Intent;
import android.net.Uri;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import android.database.Cursor;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MainActivity extends Activity implements PeerChangeCallback {
    private DataOutputStream dataOutputStream = null;
    private DataInputStream dataInputStream = null;
    private MyCustomForm form;
    private WifiDirectHandler wifidirect;
    // private CheckPermission checkpermission;
    TextView peerDevice;
    LinearLayout deviceListLayout;
    LinearLayout connectedDeviceLayout;
    private static final int FILE_REQUEST_CODE = 2001;
    private Socket socket;
    ServerSocket serverSocket;
    private boolean isThisDeviceClient = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        deviceListLayout= findViewById(R.id.deviceContainer); // the DeviceList LinearLayout
        connectedDeviceLayout = findViewById(R.id.connectedDevice);//container for connected device
        TextView text = findViewById(R.id.wifidirectTest);
        Log.d(LogTags.AWARE_ASM, "MainActivity onCreate started");
        // checkpermission = new CheckPermission(this,this);

        wifidirect = new WifiDirectHandler(this, this);
        form = new MyCustomForm(this,this);
        Button refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            Log.d(LogTags.AWARE_ASM,"refreshbutton clicked, starting discovery again");
            deviceListLayout.removeAllViews();
            wifidirect.startdiscovery();
            // if(checkpermission.isAllPermissionAccepted())
            // {
            //     wifidirect.startdiscovery();
            // }
            // else
            // {
            //     checkPermission.checkAndRequestPermissions();  // new public method
            // }
        });
        Log.d(LogTags.AWARE_ASM, "MainActivity UI successfully created");
        
    }
    @Override
    public void StartActivityForFile(Intent intent)
    {
        startActivityForResult(intent,FILE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            form.AddSendButton(connectedDeviceLayout,fileUri);
            form.showToast("Selected: " + fileUri.getLastPathSegment());
        }
    }
    @Override
    protected void onResume() {
        Log.d(LogTags.AWARE_ASM, "MainActivity UI onResume ");
        super.onResume();
        wifidirect.register();
    }
    @Override
    public void onAllPermissionsGranted()
    {
        wifidirect.startdiscovery();
    }
    @Override
    public void onPermissionNotGranted()
    {
        form.showToast("Please approve all the permission");
    }
    // @Override
    // protected void onPause() {
    //     Log.d(LogTags.AWARE_ASM, "MainActivity UI onPause ");
    //     super.onPause();
    //     wifidirect.unregister();
    // }   
    // CALLBACK IMPLEMENTATION
    @Override
    public void OnPeerChange(Collection<WifiP2pDevice>p2pDevices) {
        deviceListLayout.removeAllViews();
        if (p2pDevices.isEmpty())
        {
            form.AddDeviceNameAndMACIntoList("No P2P devices","No P2P MAC Available",deviceListLayout);
            return;
        }
        for (WifiP2pDevice dev : p2pDevices) {
            Log.d(LogTags.AWARE_ASM, "P2P Device and P2P MAC" + dev.deviceName + ": "+dev.deviceAddress);
            String peerInfo = dev.deviceName +" and "+dev.deviceAddress;
            // form.showToast("P2P search devices: "+dev.deviceName +" "+dev.deviceAddress);
            form.AddDeviceNameAndMACIntoList(dev.deviceName,dev.deviceAddress,deviceListLayout);
        }
        Log.d(LogTags.AWARE_ASM, "p2pDevices changes");
    }
    @Override
    public void ConnectToDevice(String deviceInfo)
    {
        form.showToast("connecting to " +deviceInfo);
        wifidirect.connectToDevice(deviceInfo);
    }
    @Override
    public void showToastPopup(String text)
    {
        form.showToast(text);
    }
    @Override
    public void ShowConnectedDevice(String deviceName)
    {
        form.AddDeviceToConnectedLayout(deviceName,connectedDeviceLayout);
        if(isThisDeviceClient)
        {
            form.AddFileButton(connectedDeviceLayout);
        }
        deviceListLayout.removeAllViews();
    }
    @Override 
    public void OnConnected()
    {

    }
    @Override
    public void OnDisconnected()
    {
        if(isThisDeviceClient && socket != null)
        {
            try{
                socket.close();
            }
            catch (IOException e) {
                Log.d(LogTags.AWARE_ASM,"error occured during socket close at client side"+e);
            }
            Log.d(LogTags.AWARE_ASM,"socket closed at client side");
        }
        connectedDeviceLayout.removeAllViews();
    }
    @Override
    public void DisconnectPeerDevice()
    {
        connectedDeviceLayout.removeAllViews();
        wifidirect.disconnectDevice();
    }

    @Override 
    public void CreateServerSocket()
    {
        Log.d(LogTags.AWARE_ASM,"CreateServerSocket");
        new Thread(() -> {
            try {
                if (serverSocket == null || serverSocket.isClosed()) {
                    serverSocket = new ServerSocket(8888);
                    serverSocket.setReuseAddress(true);
                    // serverSocket.bind(new InetSocketAddress(8888));
                }
                // serverSocket.setSoTimeout(60_000);

                Log.d(LogTags.AWARE_ASM, "ServerSocket created, waiting for client...");
                Socket clientSocket  = serverSocket.accept();
                Log.d(LogTags.AWARE_ASM, "Client connected: " + clientSocket.getInetAddress());
                form.showToast("client connected successfully!");
                dataInputStream = new DataInputStream(clientSocket.getInputStream());
                receiveFile(dataInputStream);
                clientSocket.close();
                Log.d(LogTags.AWARE_ASM, "socket close at server side , file received successfully!");
                form.showToast("file received successfully!");
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                        Log.d(LogTags.AWARE_ASM, "ServerSocket closed");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                Log.d(LogTags.AWARE_ASM,"error occured "+e);
            }
            catch (Exception e)
            {
                Log.d(LogTags.AWARE_ASM,"error occured in file receiving"+e);
            }
        }).start();

    }

    @Override
    public void CreateSocketAtClient(String GOIp)
    {
        isThisDeviceClient = true;
        Log.d(LogTags.AWARE_ASM,"CreateSocketAtClient and added sleep of 3 second");
        new Thread(() -> {
            try {
                Log.d(LogTags.AWARE_ASM,"thread started..");
                Thread.sleep(3000); // wait for server
                Log.d(LogTags.AWARE_ASM,"thread sleep over started..");
                if(socket == null)
                {
                    socket = new Socket();
                    Log.d(LogTags.AWARE_ASM,"created socket at client side");
                }
                Log.d(LogTags.AWARE_ASM,"new socket created..");
                Log.d(LogTags.AWARE_ASM, "Connecting to server at " + GOIp + ":8888");
                if(GOIp.isEmpty())
                {
                    Log.d(LogTags.AWARE_ASM,"GOIP is null..");
                    return;
                }
                socket.connect(new InetSocketAddress(GOIp, 8888), 5000); // 5s timeout
                Log.d(LogTags.AWARE_ASM, "Connected to server");
                form.showToast("connected to server successfully!");
                // socket.setSoTimeout(60_000);
            }
            catch (InterruptedException e)
            {
                Log.d(LogTags.AWARE_ASM,"error occured "+e);

            } 
            catch (IOException e) {
                Log.d(LogTags.AWARE_ASM,"error occured "+e);
            }
        }).start();
    }
    @Override
    public void SendFileToServer(Uri fileUri)
    {
        form.showToast("sending file....");
        try {
                dataOutputStream = new DataOutputStream(
                socket.getOutputStream());
                sendFile(fileUri);

            } catch (IOException e) {
                Log.e(LogTags.AWARE_ASM, "Socket IO error", e);
            } catch (Exception e) {
                Log.e(LogTags.AWARE_ASM, "Send file error", e);
            }
    }
    // receive file function is start here

    private void receiveFile(DataInputStream dataInputStream)
        throws Exception
    {
        String fileName = dataInputStream.readUTF();
        Log.d(LogTags.AWARE_ASM, "Receiving file: " + fileName);

        long fileSize = dataInputStream.readLong();
        Log.d(LogTags.AWARE_ASM, "File size: " + fileSize);
        File externalDir = new File(getExternalFilesDir(null), "WIFIDIRECT");
        if (!externalDir.exists()) {
            boolean created = externalDir.mkdirs();
            if (created) {
                Log.d(LogTags.AWARE_ASM, "WIFIDIRECT folder created in external storage");
            } else {
                Log.d(LogTags.AWARE_ASM, "Failed to create WIFIDIRECT folder");
            }
        }

        File receivedFile = new File(externalDir, fileName);
        FileOutputStream fos = new FileOutputStream(receivedFile);
        Log.d(LogTags.AWARE_ASM, "FileOutputStream created success..");
        int bytesRead;
        long totalRead = 0;
        byte[] buffer = new byte[4 * 1024];

        while (totalRead < fileSize && (bytesRead = dataInputStream.read(buffer, 0,
                (int)Math.min(buffer.length, fileSize - totalRead))) != -1) {
            fos.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
        }
        // Here we received file
        Log.d(LogTags.AWARE_ASM, "File stored at: " + receivedFile.getAbsolutePath());
        // form.showToast("File stored at: " + receivedFile.getAbsolutePath());
        fos.flush();
        fos.close();
        dataInputStream.close();
    }
    // sendFile function define here
    private void sendFile(Uri fileUri)
        throws Exception
    {
        int bytes = 0;
        InputStream fileInput = getContentResolver().openInputStream(fileUri);
        if (fileInput == null) {
            Log.e(LogTags.AWARE_ASM, "Unable to open file input stream");
            return;
        }
        String fileName = getFileName(fileUri);
        dataOutputStream.writeUTF(fileName);
        long fileSize = getFileSize(fileUri);

        dataOutputStream.writeLong(fileSize);
        byte[] buffer = new byte[4 * 1024];
        while ((bytes = fileInput.read(buffer))!= -1) {
            dataOutputStream.write(buffer, 0, bytes);
            dataOutputStream.flush();
        }
        // close the file here
        Log.d(LogTags.AWARE_ASM,"file is sent");
        fileInput.close();
    }
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private long getFileSize(Uri uri) {
        long size = 0;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (sizeIndex >= 0) size = cursor.getLong(sizeIndex);
                }
            }
        }
        return size;
    }


}
