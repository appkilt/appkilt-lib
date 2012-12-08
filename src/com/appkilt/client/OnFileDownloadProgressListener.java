package com.appkilt.client;

interface OnFileDownloadProgressListener {
	public abstract void onProgressUpdate(long dl_uid, long bytesDownloaded, long total);
}
