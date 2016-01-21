/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.clouds.interfaces.transferadapters;

import com.lahenry.nimbus.clouds.interfaces.ICloudController;
import com.lahenry.nimbus.clouds.interfaces.ICloudProgress;
import com.lahenry.nimbus.clouds.interfaces.ICloudTransfer;
import com.lahenry.nimbus.utils.GlobalCache;
import com.lahenry.nimbus.utils.GlobalCacheKey;
import com.lahenry.nimbus.utils.Logit;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author henry
 * @param <S> = Source object
 * @param <T> = Target object
 */
public abstract class CloudTransferAdapter<S, T>
    implements ICloudTransfer<S, T>
{
    private static final Logit Log = Logit.create(CloudTransferAdapter.class.getName());

    protected final GlobalCacheKey m_sourceCacheKey;
    protected final S m_source;

    protected final GlobalCacheKey m_targetCacheKey;
    protected final T m_target;

    // the returned object after it was transfered
    protected T m_xferred;

    protected ICloudProgress m_progress;

    protected AtomicBoolean m_canTransfer;

    public CloudTransferAdapter(GlobalCacheKey sourceCacheKey,
                                S source,
                                GlobalCacheKey targetCacheKey,
                                T target)
    {
        Log.entering("<init>", new Object[]{sourceCacheKey, source, targetCacheKey, target});

        m_sourceCacheKey = sourceCacheKey;
        m_source = source;

        m_targetCacheKey = targetCacheKey;
        m_target = target;
    }

    @Override
    public S getSourceObject()
    {
        return m_source;
    }

    @Override
    public T getTargetObject()
    {
        return m_target;
    }

    @Override
    public void setTransferredObject(T obj)
    {
        m_xferred = obj;
    }

    @Override
    public T getTransferredObject()
    {
        return m_xferred;
    }

    @SuppressWarnings("unchecked")
    @Override
    public InputStream getInputStream()
    {
        // caller must close inputstream;
        ICloudController<S> controller = (ICloudController<S>)GlobalCache.getInstance().get(m_sourceCacheKey);
        return controller.getDownloadStream(getSourceObject());
    }

    @Override
    public void setProgressHandler(ICloudProgress progress)
    {
        m_progress = progress;
    }

    @Override
    public ICloudProgress getProgressHandler()
    {
        return m_progress;
    }

    @Override
    public void setCanTransfer(AtomicBoolean canTransfer)
    {
        m_canTransfer = canTransfer;
    }

    @Override
    public boolean getCanTransfer()
    {
        return m_canTransfer.get();
    }
}
