package com.lukeleber.scandroid.interpreter.binary;

import com.lukeleber.scandroid.interpreter.AbstractInterpreter;
import com.lukeleber.scandroid.interpreter.FailureCode;
import com.lukeleber.scandroid.interpreter.Request;
import com.lukeleber.scandroid.interpreter.ResponseListener;
import com.lukeleber.scandroid.interpreter.ServiceRequest;
import com.lukeleber.scandroid.io.CommunicationInterface;

import java.io.IOException;

/**
 * Skeletal layout for a future open source Arduino based interpreter.  Currently, simply a
 * mock placeholder to ensure that the interpreter package hierarchies are flexible enough...
 */
@SuppressWarnings("unused")
public abstract class NativeInterpreter
    extends
        AbstractInterpreter<byte[]>
{
    /**
     * Constructs an {@link com.lukeleber.scandroid.interpreter.AbstractInterpreter}
     *
     * @param com
     */
    protected NativeInterpreter(CommunicationInterface com) {
        super(com);
    }

    /**
     * Performs any initialization before entering the I/O loop.  This is an ideal spot to pass any
     * configurable options to the interpreter hardware.  Overrides of this method are required
     * to call through to the super method implementation.
     *
     * @throws java.io.IOException if any I/O error occurs during initialization
     */
    @Override
    protected native void init() throws IOException;

    protected native void _writeRequest(byte[] request) throws IOException;

    /**
     * Performs the actual writing operation to the remote hardware.  The implementation details of
     * this method will be dependent on the type of remote system being interacted with.
     *
     * @param request the {@link com.lukeleber.scandroid.interpreter.Request} to write
     * @throws java.io.IOException if any I/O error occurs during the write
     */
    @Override
    protected void writeRequest(Request<?> request) throws IOException
    {
        byte[] buffer = null;
        if(request instanceof ServiceRequest)
        {
            ServiceRequest<?> sr = (ServiceRequest)request;
            int svc = sr.getService().getID();
            int id = sr.getPID().getID();
            buffer = new byte[]
            {
                    (byte)(svc >> 8),
                    (byte)(svc & 0xFF),
                    (byte)(id >> 8),
                    (byte)(id & 0xFF)
            };
        }
        _writeRequest(buffer);
    }

    /**
     * Performs the actual reading operation from the remote hardware.  The implementation details
     * of this method will be dependent on the type of remote system being interacted with.
     *
     * @return the data that was read
     * @throws java.io.IOException if any I/O error occurs during the read
     */
    @Override
    protected native byte[] readReply() throws IOException;

    /**
     * Retrieves a user-friendly name of this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     *
     * @return a user-friendly name of this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     * @since 1.0
     */
    @Override
    public String getName() {
        return "Native Interpreter";
    }

    /**
     * Sends an asynchronous request over this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     * and invokes a default {@link com.lukeleber.scandroid.interpreter.ResponseListener} on the UI thread when a reply is received, the
     * request times out, or an error occurs.
     *
     * @param request the {@link com.lukeleber.scandroid.interpreter.Request} to send over this {@link com.lukeleber.scandroid.interpreter.Interpreter}
     * @since 1.0
     */
    @Override
    public <V> void sendRequest(final Request<V> request)
    {
        super.sendRequest(request,
            new ResponseListener<byte[]>()
            {

                @Override
                public void onSuccess(byte[] message)
                {
                    /// message contains the raw data (including header bytes / ID bits)
                    /// So specialized processing might be required for dealing with out of
                    /// order responses!
                    /// TODO: Later :)
                    request.getHandler().onResponse(request.getUnmarshaller().invoke(message));
                }

                @Override
                public void onFailure(FailureCode code)
                {
                    request.getHandler().onFailure(code);
                }
            }
        );
    }

    @Override
    public native void close() throws IOException;
}
