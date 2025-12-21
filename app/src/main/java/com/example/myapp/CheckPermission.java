// import android.Manifest;
// import android.app.Activity;
// import android.content.pm.PackageManager;
// import android.os.Build;

// import androidx.annotation.NonNull;
// import androidx.core.app.ActivityCompat;
// import androidx.core.content.ContextCompat;

// import java.util.ArrayList;
// import java.util.List;

// public class CheckPermission {

//     private Activity activity;  // use Activity for permissions
//     private PeerChangeCallback cb;
//     private boolean isAllPermissionAccepted = false;
//     private static final int PERMISSION_REQUEST_CODE = 1001;

//     public CheckPermission(Activity activity, PeerChangeCallback cb) {
//         this.activity = activity;
//         this.cb = cb;
//         checkAndRequestPermissions();
//     }

//     private String[] getRequiredPermissions() {
//         List<String> permissions = new ArrayList<>();

//         // Wi-Fi / Location
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//             permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES);
//         } else {
//             permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//         }

//         // Storage
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//             permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
//             permissions.add(Manifest.permission.READ_MEDIA_VIDEO);
//             permissions.add(Manifest.permission.READ_MEDIA_AUDIO);
//         } else {
//             permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//             permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//         }

//         return permissions.toArray(new String[0]);
//     }

//     public void checkAndRequestPermissions() {
//         List<String> missingPermissions = new ArrayList<>();

//         for (String permission : getRequiredPermissions()) {
//             if (context.checkSelfPermission(activity, permission)
//                     != PackageManager.PERMISSION_GRANTED) {
//                 missingPermissions.add(permission);
//             }
//         }

//         if (!missingPermissions.isEmpty()) {
//             ActivityCompat.requestPermissions(
//                     activity,
//                     missingPermissions.toArray(new String[0]),
//                     PERMISSION_REQUEST_CODE
//             );
//         } else {
//             isAllPermissionAccepted = true;
//             cb.onAllPermissionsGranted();
//         }
//     }

//     // Call this from the Activity's onRequestPermissionsResult
//     public void handlePermissionsResult(int requestCode,
//                                         @NonNull String[] permissions,
//                                         @NonNull int[] grantResults) {
//         if (requestCode == PERMISSION_REQUEST_CODE) {
//             boolean allGranted = true;
//             for (int result : grantResults) {
//                 if (result != PackageManager.PERMISSION_GRANTED) {
//                     allGranted = false;
//                     break;
//                 }
//             }
//             if (allGranted) {
//                 isAllPermissionAccepted = true;
//                 cb.onAllPermissionsGranted();
//             } else {
//                 cb.onPermissionNotGranted();
//             }
//         }
//     }

//     public boolean isAllPermissionGranted() {
//         return isAllPermissionAccepted;
//     }
// }
