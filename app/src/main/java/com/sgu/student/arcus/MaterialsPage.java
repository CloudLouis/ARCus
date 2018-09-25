package com.sgu.student.arcus;

import android.Manifest;
import android.app.NotificationManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.SimpleArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.sgu.student.arcus.db.Database;
import com.sgu.student.arcus.db.entity.ClassesEntity;
import com.sgu.student.arcus.db.entity.MaterialsEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MaterialsPage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MaterialsPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MaterialsPage extends Fragment implements AdapterView.OnItemSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static final int fileExplorerRequest = 1;
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private String selectedItem;
    private String file_path;
    private String advertiser_name;
    NotificationManager mNotificationManager;


    //Google Nearby variables
    private String targetId;
    public static final Strategy STRATEGY = Strategy.P2P_STAR;
    private static final String TAG = "ARCus connection status";
    private Button connectButton;
    private Button stopConnect;
    private Button advertiseButton;
    private Button stopAdvertise;
    private ConnectionsClient connectionsClient;
    private static final String[] REQUIRED_PERMISSIONS =
            new String[] {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
            };
    //until here

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public MaterialsPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MaterialsPage.
     */
    // TODO: Rename and change types and number of parameters
    public static MaterialsPage newInstance(String param1, String param2) {
        MaterialsPage fragment = new MaterialsPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_materials_page, container, false);

        ImageView reveal_add_button = v.findViewById(R.id.add_new_material_button);
        reveal_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getView().findViewById(R.id.new_material_field).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.add_new_material_button).setVisibility(View.GONE);
                getView().findViewById(R.id.cancel_new_material_button).setVisibility(View.VISIBLE);
                }
        });
        ImageView cancel_add_button = v.findViewById(R.id.cancel_new_material_button);
        cancel_add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getView().findViewById(R.id.new_material_field).setVisibility(View.GONE);
                getView().findViewById(R.id.add_new_material_button).setVisibility(View.VISIBLE);
                getView().findViewById(R.id.cancel_new_material_button).setVisibility(View.GONE);

            }
        });
        Button browse = v.findViewById(R.id.browse_button);
        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                getFile();

            }
        });

        Button confirm = v.findViewById(R.id.confirm_material_add);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMaterial();
            }
        });

        Spinner spinner = (Spinner) v.findViewById(R.id.materials_class_list);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                selectedItem = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
        Database database = Room.databaseBuilder(getContext(), Database.class, "mainDB").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        List<ClassesEntity> classesList = database.getClassesDao().getClassesEntityList();
        List<String> classesName = new ArrayList<>();
        for(ClassesEntity x : classesList){
            classesName.add(x.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, classesName);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        MaterialsEntity[] materials = database.getMaterialsDao().getMaterialsEntity();

        mRecyclerView = v.findViewById(R.id.materials_list);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        ArrayList<MaterialsEntity> dataset = new ArrayList<>();
        for(int i = 0;i<materials.length;i++){
            dataset.add(materials[i]);
        }
        mAdapter = new MaterialsListAdapter(this.getContext(),dataset, this);
        mRecyclerView.setAdapter(mAdapter);

        connectButton = v.findViewById(R.id.connect_button);
        stopConnect = v.findViewById(R.id.stop_connect_button);
        advertiseButton = v.findViewById(R.id.advertise_button);
        stopAdvertise = v.findViewById(R.id.stop_advertise_button);
        advertiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginAdvertising();
            }
        });
        stopAdvertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAdvertise();
            }
        });
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beginDiscovering();
            }
        });
        stopConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopDiscovery();
            }
        });
        connectionsClient = Nearby.getConnectionsClient(getContext());
        mNotificationManager = (NotificationManager)getContext().getSystemService( getContext().NOTIFICATION_SERVICE );
        return v;
    }

    public void saveMaterial(){
        TextView tv = getView().findViewById(R.id.new_material_name);
        Database database = Room.databaseBuilder(getContext(), Database.class, "mainDB").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        List<ClassesEntity> selectedClass = database.getClassesDao().retrieveByName(selectedItem);

        MaterialsEntity materials = new MaterialsEntity();
        materials.setTitle(tv.getText().toString());
        materials.setPath(file_path);
        materials.setClass_id(selectedClass.get(0).getC_id());
        database.getMaterialsDao().insert(materials);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == fileExplorerRequest) {
            if(ExFilePickerResult.getFromIntent(data)!=null){
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            Uri uri = data.getData();
                if (result != null && result.getCount() > 0) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < result.getCount(); i++) {
                        stringBuilder.append(result.getNames().get(i));
                        if (i < result.getCount() - 1) stringBuilder.append(", ");
                    }
                    file_path = result.getPath() + stringBuilder.toString();
                    TextView tv = getView().findViewById(R.id.new_material_file);
                    tv.setText(file_path);

                }
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void getFile(){
        ExFilePicker filePicker = new ExFilePicker();
        filePicker.start(this, fileExplorerRequest);
    }

    public void beginAdvertising(){
        if (!hasPermissions(getContext(), REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
        startAdvertising();
        advertiseButton.setVisibility(View.GONE);
        stopAdvertise.setVisibility(View.VISIBLE);
    }

    public void beginDiscovering(){
        if (!hasPermissions(getContext(), REQUIRED_PERMISSIONS)) {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
        startDiscovery();
        connectButton.setVisibility(View.GONE);
        stopConnect.setVisibility(View.VISIBLE);

    }

    //Permission handling
    /** Returns true if the app was granted all the permissions. Otherwise, returns false. */
    private static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    /** Handles user acceptance (or denial) of our permission request. */
    @CallSuper
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return;
        }

        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getActivity(), R.string.error_missing_permissions, Toast.LENGTH_LONG).show();
                getActivity().finish();
                return;
            }
        }
        getActivity().recreate();
    }

    //Google Nearby functions
    /** Starts looking for other players using Nearby Connections. */
    private void startDiscovery() {
        // Note: Discovery may fail. To keep this demo simple, we don't handle failures.
        connectionsClient.startDiscovery(
                getActivity().getPackageName(), endpointDiscoveryCallback, new DiscoveryOptions(STRATEGY));
    }

    private void stopDiscovery(){
        connectionsClient.stopAllEndpoints();
        connectionsClient.stopDiscovery();
        connectButton.setVisibility(View.VISIBLE);
        stopConnect.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "No longer discovering", Toast.LENGTH_SHORT).show();
    }

    /** Broadcasts our presence using Nearby Connections so other players can find us. */
    private void startAdvertising() {
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        EditText temp = getView().findViewById(R.id.mtrl_nameInput);
        String advertising_name = temp.getText().toString();
        connectionsClient.startAdvertising(
                advertising_name,getActivity().getPackageName(), connectionLifecycleCallback, new AdvertisingOptions(STRATEGY));
    }

    private void stopAdvertise(){
        connectionsClient.stopAllEndpoints();
        connectionsClient.stopAdvertising();
        advertiseButton.setVisibility(View.VISIBLE);
        stopAdvertise.setVisibility(View.GONE);
        Toast.makeText(getActivity(), "All current connection severed. No longer advertising!", Toast.LENGTH_SHORT).show();
    }

    public void sendFile(File f, String s) throws FileNotFoundException, UnsupportedEncodingException {
        connectionsClient.sendPayload(targetId, Payload.fromBytes(s.getBytes("UTF-8")));
        connectionsClient.sendPayload(targetId, Payload.fromFile(f));
    }

    // Callbacks for receiving payloads
    private final PayloadCallback payloadCallback =
            new PayloadCallback() {
                Payload check;
                Payload incomingFile;
                private final SimpleArrayMap<Long, String> filePayloadFilenames = new SimpleArrayMap<>();
                private final SimpleArrayMap<Long, NotificationCompat.Builder> incomingPayloads = new SimpleArrayMap<>();
                private final SimpleArrayMap<Long, NotificationCompat.Builder> outgoingPayloads = new SimpleArrayMap<>();
                String fileName;

                private void sendPayload(String endpointId, Payload payload) {
                    if (payload.getType() == Payload.Type.BYTES) {
                        // No need to track progress for bytes.
                        return;
                    }

                    // Build and start showing the notification.
                    NotificationCompat.Builder notification = buildNotification(payload, false /*isIncoming*/);
                    mNotificationManager.notify((int) payload.getId(), notification.build());

                    // Add it to the tracking list so we can update it.
                    outgoingPayloads.put(payload.getId(), notification);
                }

                private NotificationCompat.Builder buildNotification(Payload payload, boolean isIncoming) {
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(getContext())
                            .setContentTitle(isIncoming ? "Receiving..." : "Sending...")
                            .setSmallIcon(R.drawable.file_notif_1);
                    int size = (int) payload.asFile().getSize();
                    boolean indeterminate = false;
                    if (size == -1) {
                        // This is a stream payload, so we don't know the size ahead of time.
                        size = 100;
                        indeterminate = true;
                    }
                    notification.setProgress(size, 0, indeterminate);
                    return notification;
                }

                @Override
                public void onPayloadReceived(String endpointId, Payload payload) {
                    check = payload;
                    if (payload.getType() == Payload.Type.BYTES) {
                        String payloadFilenameMessage = null;
                        try {
                            payloadFilenameMessage = new String(payload.asBytes(), "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        addPayloadFilename(payloadFilenameMessage);

                    } else if (payload.getType() == Payload.Type.FILE) {
                        // Add this to our tracking map, so that we can retrieve the payload later.
                        incomingFile = payload;

                        // Build and start showing the notification.
                        NotificationCompat.Builder notification = buildNotification(payload, true /*isIncoming*/);
                        mNotificationManager.notify((int) payload.getId(), notification.build());

                        // Add it to the tracking list so we can update it.
                        incomingPayloads.put(payload.getId(), notification);
                    }
                }

                private void addPayloadFilename(String payloadFilenameMessage) {
                    fileName = payloadFilenameMessage;
                }

                @Override
                public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                    if (check != null) {
                        if (check.getType() == Payload.Type.FILE) {
                            long payloadId = update.getPayloadId();
                            NotificationCompat.Builder notification;
                            notification = null;
                            if (incomingPayloads.containsKey(payloadId)) {
                                notification = incomingPayloads.get(payloadId);
                                if (update.getStatus() != PayloadTransferUpdate.Status.IN_PROGRESS) {
                                    // This is the last update, so we no longer need to keep track of this notification.
                                    incomingPayloads.remove(payloadId);
                                }
                            } else if (outgoingPayloads.containsKey(payloadId)) {
                                notification = outgoingPayloads.get(payloadId);
                                if (update.getStatus() != PayloadTransferUpdate.Status.IN_PROGRESS) {
                                    // This is the last update, so we no longer need to keep track of this notification.
                                    outgoingPayloads.remove(payloadId);
                                }
                            }
                            switch (update.getStatus()) {
                                case PayloadTransferUpdate.Status.IN_PROGRESS:
                                    int size = (int) update.getTotalBytes();
                                    if (size == -1) {
                                        // This is a stream payload, so we don't need to update anything at this point.
                                        return;
                                    }
                                    notification.setProgress(size, (int) update.getBytesTransferred(), false /* indeterminate */);
                                    break;
                                case PayloadTransferUpdate.Status.SUCCESS:
                                    // SUCCESS always means that we transferred 100%.
                                    if (check != null) {
                                        if (check.getType() == Payload.Type.FILE) {
                                            Payload payload = incomingFile;
                                            if (payload.getType() == Payload.Type.FILE) {
                                                // Retrieve the filename that was received in a bytes payload.
                                                String newFilename = fileName;

                                                File payloadFile = payload.asFile().asJavaFile();

                                                // Rename the file.
                                                payloadFile.renameTo(new File(payloadFile.getParentFile(), newFilename));
                                                Toast.makeText(getActivity(), "Saved in: " + payloadFile.getPath(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                    notification
                                            .setProgress(100, 100, false /* indeterminate */)
                                            .setContentText("Transfer complete!");
                                    break;
                                case PayloadTransferUpdate.Status.FAILURE:
                                    notification
                                            .setProgress(0, 0, false)
                                            .setContentText("Transfer failed");
                                    break;
                            }

                            mNotificationManager.notify((int) payloadId, notification.build());
                        }
                    }
                }
            };

    // Callbacks for connections to other devices
    private final ConnectionLifecycleCallback connectionLifecycleCallback =
            new ConnectionLifecycleCallback(){
                @Override
                public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
                    Log.i(TAG, "onConnectionInitiated: accepting connection");
                    connectionsClient.acceptConnection(endpointId, payloadCallback);
                    advertiser_name = connectionInfo.getEndpointName();
                }

                @Override
                public void onConnectionResult(String endpointId, ConnectionResolution result) {
                    if (result.getStatus().isSuccess()) {
                        Log.i(TAG, "onConnectionResult: connection successful");
                        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_LONG).show();
                        TextView temp = getView().findViewById(R.id.mtrl_connectedName);
                        temp.append(advertiser_name+" ");
                        targetId = endpointId;
                    } else {
                        Log.i(TAG, "onConnectionResult: connection failed");
                    }
                }

                @Override
                public void onDisconnected(String endpointId) {
                    Log.i(TAG, "onDisconnected: disconnected from the opponent");
                }
            };

    // Callbacks for finding other devices
    private final EndpointDiscoveryCallback endpointDiscoveryCallback =
            new EndpointDiscoveryCallback() {
                @Override
                public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                    EditText temp = getView().findViewById(R.id.mtrl_request_name_input);
                    String request_name = temp.getText().toString();
                    Log.i(TAG, "onEndpointFound: endpoint found, connecting");
                    connectionsClient.requestConnection(request_name, endpointId, connectionLifecycleCallback);
                }

                @Override
                public void onEndpointLost(String endpointId) {}
            };
}
