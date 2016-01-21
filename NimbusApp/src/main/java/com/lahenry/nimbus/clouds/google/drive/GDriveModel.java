/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.google.drive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.lahenry.nimbus.accountmanager.AccountInfo;
import com.lahenry.nimbus.accountmanager.AccountManager;
import com.lahenry.nimbus.clouds.CloudType;
import com.lahenry.nimbus.clouds.interfaces.ICloudModel;
import com.lahenry.nimbus.clouds.interfaces.ICloudProgress;
import com.lahenry.nimbus.clouds.interfaces.ICloudTransfer;
import com.lahenry.nimbus.mainapp.AppInfo;
import com.lahenry.nimbus.utils.GlobalCache;
import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Logit;
import com.lahenry.nimbus.utils.Tools;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author henry
 */
public class GDriveModel implements ICloudModel<com.google.api.services.drive.model.File>
{
    private static final Logit Log = Logit.create(GDriveModel.class.getName());

    private static final String CLIENT_ID = "377040850517-vc3hbqvqqct5svp9nrdagrhg2v06v0o2.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "-ezNN3hvssAwm6Ewgmrg69pI";

    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    private static final String SECRET_ACCESS = "access";
    private static final String SECRET_REFRESH = "refresh";

    private final GoogleAuthorizationCodeFlow m_flow;
    private Drive m_service;

    private About m_userInfo;

    private File m_root;

    public GDriveModel()
    {
        Log.entering("<init>");

        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();

        Log.fine("Creating new authorization flow");
        GoogleAuthorizationCodeFlow.Builder flowBuilder = new GoogleAuthorizationCodeFlow
            .Builder(httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
            .setAccessType("offline")
            .setApprovalPrompt("auto");

        Log.fine("Building new authorization flow");
        m_flow = flowBuilder.build();
    }

    @Override
    public GlobalCacheKey getGlobalCacheKey()
    {
        return GlobalCache.getInstance().getKey(this);
    }

    @Override
    public String getAuthUrl()
    {
        Log.entering("getAuthUrl");
        String url = m_flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
        Log.info(url);
        return url;
    }

    @Override
    public boolean loginViaAuthCode(String authCode)
    {
        Log.entering("loginViaAuthCode", new Object[]{authCode});

        m_service = null; // make sure the previous object is released

        if (Tools.isNullOrEmpty(authCode))
        {
            Log.severe("Auth code is empty");
            return false;
        }

        try
        {
            Log.fine("Requesting access token");
            GoogleTokenResponse response = m_flow.newTokenRequest(authCode).setRedirectUri(REDIRECT_URI).execute();

            if ((response == null) || response.isEmpty())
            {
                Log.severe("Response is null or empty");
                return false;
            }

            Log.fine("Response is " + response.toString());

            Credential creds = m_flow.createAndStoreCredential(response, null);

            return loginViaAccessToken(creds.getAccessToken(), creds.getRefreshToken());
        }
        catch (IOException ex)
        {
            Log.throwing("login", ex);
            return false;
        }
    }

    protected boolean loginViaAccessToken(String accesstoken, String refreshtoken)
    {
        Log.entering("loginViaAccessToken", new Object[]{accesstoken, refreshtoken});

        if (Tools.isNullOrEmpty(accesstoken))
        {
            Log.warning("Access token is emtpy");
            return false;
        }

        if (Tools.isNullOrEmpty(refreshtoken))
        {
            Log.warning("Refresh token is emtpy");
            return false;
        }

        Log.fine("Using stored credentials");

        HttpTransport httpTransport = m_flow.getTransport();
        JsonFactory jsonFactory = m_flow.getJsonFactory();

        Log.fine("Creating new GoogleCredential using stored credentials");
        GoogleCredential credential = new GoogleCredential.Builder()
            .setJsonFactory(jsonFactory)
            .setTransport(httpTransport)
            .setClientSecrets(CLIENT_ID, CLIENT_SECRET)
            .build()
            .setAccessToken(accesstoken)
            .setRefreshToken(refreshtoken);

        //Create a new authorized API client
        Log.fine("Creating the new Google Drive client");
        m_service = new Drive.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName(AppInfo.Name)
            .build();

        try
        {
            Log.fine("Getting user info");
            m_userInfo = m_service.about().get().execute();
        }
        catch (IOException ex)
        {
            Log.throwing("loginViaAccessToken", ex);
            m_userInfo = null;
        }

        if (m_userInfo != null)
        {
            AccountManager manager = AccountManager.getInstance();
            if (manager != null)
            {
                AccountInfo info = AccountInfo.createInstance(CloudType.GOOGLE_DRIVE, getUniqueId());
                info.setName(getDisplayName());
                info.addSecret(SECRET_ACCESS, accesstoken);
                info.addSecret(SECRET_REFRESH, refreshtoken);

                manager.addAccountInfo(info);
            }

            manager.exportAsFile();
        }

        getRoot();

        return true;
    }

    @Override
    public boolean loginViaStoredId(String uniqueid)
    {
        Log.entering("loginViaStoredId", new Object[]{uniqueid});

        AccountManager manager = AccountManager.getInstance();
        if (manager == null)
        {
            Log.fine("Failed to get account manager");
            return false;
        }

        AccountInfo info = manager.getAccountInfo(uniqueid);

        String accesstoken = info.getSecret(SECRET_ACCESS);
        String refreshtoken = info.getSecret(SECRET_REFRESH);

        return loginViaAccessToken(accesstoken, refreshtoken);
    }

    @Override
    public String getUniqueId()
    {
        return getEmail();
    }

    @Override
    public String getDisplayName()
    {
        return (m_userInfo != null) ? m_userInfo.getUser().getDisplayName(): null;
    }

    @Override
    public String getEmail()
    {
        return (m_userInfo != null) ? m_userInfo.getUser().getEmailAddress() : null;
    }

    @Override
    public File getRoot()
    {
        Log.entering("getRoot");

        if (m_root != null)
        {
            return m_root;
        }

        try
        {
            About about = m_service.about().get().execute();

            String rootID = about.getRootFolderId();

            Log.info("Root ID: "+rootID);

            m_root = getItemById(rootID);
        }
        catch (IOException ex)
        {
            Log.throwing("getRoot", ex);
        }

        return m_root;
    }

    @Override
    public File getItemById(String id)
    {
        Log.entering("getItemById", id);

        try
        {
            return m_service.files().get(id).execute();
        }
        catch (IOException ex)
        {
            Log.throwing("getItemById", ex);
        }
        return null;
    }

    @Override
    public String getIdByItem(File item)
    {
        return item.getId();
    }

    @Override
    public List<File> getChildrenItems(File parent)
    {
        Log.entering("getChildrenItems", new Object[]{(parent != null ? parent.getId() : "(parent.null)")});

        final List<File> list = new ArrayList<>();

        try
        {
            Drive.Children.List request = m_service.children().list(parent.getId());

            request.setQ("trashed=false");

            do {
                try {
                    ChildList children = request.execute();

                    for (ChildReference child : children.getItems()) {

                        File file = m_service.files().get(child.getId()).execute();

                        ///if (file.getLabels().getTrashed()) continue;

                        list.add(file);

                    }
                    request.setPageToken(children.getNextPageToken());
                } catch (IOException ex) {
                    Log.throwing("getChildrenItems", ex);
                    request.setPageToken(null);
                }
            } while (request.getPageToken() != null &&
                    request.getPageToken().length() > 0);

        } catch (IOException ex)
        {
            Log.throwing("getChildrenItems", ex);
        }

        return list;
    }

    @Override
    public boolean isFolder(File item)
    {
        return item.getMimeType().equals(GDriveConstants.MIME_TYPE_FOLDER);
    }

    @Override
    public String getName(File item)
    {
        return item.getTitle();
    }

    @Override
    public String getAbsolutePath(File item)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void transfer(final ICloudTransfer<?, com.google.api.services.drive.model.File> transfer)
    {
        // https://code.google.com/p/google-api-java-client/wiki/MediaUpload
        // http://stackoverflow.com/questions/25288849/resumable-uploads-google-drive-sdk-for-android-or-java

        Log.entering("transfer");

        try
        {
            final InputStream stream  = transfer.getInputStream();
            final File metadata = (File)transfer.getTargetObject();
            final ICloudProgress progressHandler = transfer.getProgressHandler();

            InputStreamContent mediaContent = new InputStreamContent(metadata.getMimeType(), stream);
            mediaContent.setLength(metadata.getFileSize());

            MediaHttpUploaderProgressListener progressListener = new MediaHttpUploaderProgressListener()
            {
                @Override
                public void progressChanged(MediaHttpUploader mhu) throws IOException
                {
                    switch (mhu.getUploadState()) {
                        case INITIATION_STARTED:
                            Log.fine("Initiation has started!");
                            progressHandler.initalize();
                            progressHandler.start(metadata.getFileSize());
                            break;
                        case INITIATION_COMPLETE:
                            Log.fine("Initiation is complete!");
                            break;
                        case MEDIA_IN_PROGRESS:
                            //Log.finer("BytesSent: "+mhu.getNumBytesUploaded()+" Progress: "+mhu.getProgress());
                            progressHandler.progress(mhu.getNumBytesUploaded());
                            break;
                        case MEDIA_COMPLETE:
                            Log.fine("Upload is complete!");
                            progressHandler.finish();
                            break;
                    }
                }
            };

            Drive.Files.Insert request = m_service.files().insert(metadata, mediaContent);
            request.getMediaHttpUploader()
                .setChunkSize(2*MediaHttpUploader.MINIMUM_CHUNK_SIZE)
                .setProgressListener(progressListener);

            Log.fine("Start uploading file");

            final long startTime = System.nanoTime();
            File xferredFile = request.execute();

            final long elapsedNano = System.nanoTime() - startTime;
            Log.fine(Tools.formatTransferMsg(elapsedNano, xferredFile.getFileSize()));

            Log.fine("Uploaded file done");

            transfer.setTransferredObject(xferredFile);
        }
        catch (IOException ex)
        {
            Log.throwing("transfer", ex);
        }
    }

    @Override
    public InputStream getDownloadStream(File downloadFile)
    {
        Log.entering("getDownloadStream", new Object[]{downloadFile.getTitle()});

        if (Tools.isNullOrEmpty(downloadFile.getDownloadUrl()))
        {
            Log.warning("Download stream URL is empty");
            return null;
        }

        try
        {
            MediaHttpDownloaderProgressListener progressListener = new MediaHttpDownloaderProgressListener()
            {
                @Override
                public void progressChanged(MediaHttpDownloader downloader) throws IOException
                {
                    Log.entering("progressChanged");
                    switch (downloader.getDownloadState())
                    {
                        case NOT_STARTED:
                            Log.fine("Download State: NOT_STARTED");
                            break;
                        case MEDIA_IN_PROGRESS:
                            Log.fine("BytesRecieved: "+downloader.getNumBytesDownloaded()+" Progress: "+downloader.getProgress());
                            break;
                        case MEDIA_COMPLETE:
                            Log.fine("Download complete");
                            break;
                    }
                }
            };

            Log.fine("Setting up the download: "+downloadFile.getTitle());
            Log.fine("Download size: "+downloadFile.getFileSize());
            Log.fine(downloadFile.toString());

            //final int CHUNK_SIZE = 4*MediaHttpUploader.MINIMUM_CHUNK_SIZE;

            Drive.Files.Get request = m_service.files().get(downloadFile.getId());
            request.getMediaHttpDownloader()
                //.setChunkSize(2*CHUNK_SIZE)
                .setProgressListener(progressListener);

            //Log.fine("ChunkSize: " + request.getMediaHttpDownloader().getChunkSize());

            InputStream is = request.executeMediaAsInputStream();

            return is;
        }
        catch (IOException ex)
        {
            Log.throwing("getDownloadStream", ex);
        }

        return null;
    }
}
