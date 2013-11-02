package org.ultimatecom;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

class RingBufferCharSequence implements CharSequence, Serializable {

    private static final long serialVersionUID = 0L;

    private char[] buffer;
    private int startIdx = 0;
    private int bufferFill = 0;
    
    private Charset currentCharset;
    
    private class RingBufferCharSubSequence implements CharSequence {

        private final int startIdx;
        private final int endIdx;
        
        public RingBufferCharSubSequence(int startIdx, int endIdx) {
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }
        
        @Override
        public CharSequence subSequence(int startIdx, int endIdx) {
            return new RingBufferCharSubSequence(this.startIdx + startIdx, this.endIdx + endIdx);
        }
        
        @Override
        public int length() {
            return this.endIdx - this.startIdx;
        }
        
        @Override
        public char charAt(int index) {
            synchronized (RingBufferCharSequence.this) {
                return buffer[(RingBufferCharSequence.this.startIdx + index + this.startIdx) % buffer.length];
            }
        }
    }
    
    public RingBufferCharSequence(int bufferSize, Charset initialCharset) {
        buffer = new char[bufferSize];
        currentCharset = initialCharset;
    }

    @Override
    public synchronized char charAt(int index) {
        return buffer[(startIdx + index) % buffer.length];
    }

    @Override
    public synchronized int length() {
        return bufferFill;
    }

    @Override
    public CharSequence subSequence(int startIdx, int endIdx) {
        return new RingBufferCharSubSequence(startIdx, endIdx);
    }
    
    @Override
    public synchronized String toString() {
        if (this.startIdx == 0) {
            return new String(buffer, 0, bufferFill); 
        } else {
            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(buffer, startIdx, bufferFill - startIdx);
            strBuilder.append(buffer, 0, bufferFill);
            return strBuilder.toString();
        }
    }
    
    public final void append(byte[] data, int length) {
        char[] dataAsChars = currentCharset.decode(ByteBuffer.wrap(data, 0, length)).array();
        
        synchronized (this) {
            for (int i = 0; i < dataAsChars.length; ++i) {
                buffer[(startIdx + i + bufferFill) % buffer.length] = dataAsChars[i];
            }
            bufferFill = Math.min(bufferFill + dataAsChars.length, buffer.length);
        }
    }
    
    public final synchronized void convertToCharset(Charset newCharset) {
        if (!currentCharset.equals(newCharset)) {
            char[] newCharsetChars = newCharset.decode(currentCharset.encode(this.toString())).array();
            if (newCharsetChars.length > (buffer.length - (buffer.length / 5))) {
                buffer = newCharsetChars;
                bufferFill = buffer.length;
                
            } else {
                for (int i = 0; i < newCharsetChars.length; ++i) {
                    buffer[i] = newCharsetChars[i];
                }
                bufferFill = newCharsetChars.length;
            }
            startIdx = 0;
            currentCharset = newCharset;
        }
    }
    
    public final synchronized Charset getCurrentCharset() {
        return currentCharset;
    }
}