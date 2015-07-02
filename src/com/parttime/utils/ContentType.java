package com.parttime.utils;
/**
 * 
 * @author 灰色的寂寞
 * @function 筛选媒体类型
 * @date 2015-1-18
 * @time 14：38
 *
 */
public class ContentType {
	public static final String MMS_MESSAGE = "application/vnd.wap.mms-message";
	// The phony content type for generic PDUs (e.g. ReadOrig.ind,
	// Notification.ind, Delivery.ind).
	public static final String MMS_GENERIC = "application/vnd.wap.mms-generic";
	public static final String MULTIPART_MIXED = "application/vnd.wap.multipart.mixed";
	public static final String MULTIPART_RELATED = "application/vnd.wap.multipart.related";
	public static final String MULTIPART_ALTERNATIVE = "application/vnd.wap.multipart.alternative";

	public static final String TEXT_PLAIN = "text/plain";
	public static final String TEXT_HTML = "text/html";
	public static final String TEXT_VCALENDAR = "text/x-vCalendar";
	public static final String TEXT_VCARD = "text/x-vCard";

	public static final String IMAGE_UNSPECIFIED = "image/*";
	public static final String IMAGE_JPEG = "image/jpeg";
	public static final String IMAGE_JPG = "image/jpg";
	public static final String IMAGE_GIF = "image/gif";
	public static final String IMAGE_WBMP = "image/vnd.wap.wbmp";
	public static final String IMAGE_PNG = "image/png";
	public static final String IMAGE_X_MS_BMP = "image/x-ms-bmp";

	public static final String AUDIO_UNSPECIFIED = "audio/*";
	public static final String AUDIO_AAC = "audio/aac";
	public static final String AUDIO_AMR = "audio/amr";
	public static final String AUDIO_IMELODY = "audio/imelody";
	public static final String AUDIO_MID = "audio/mid";
	public static final String AUDIO_MIDI = "audio/midi";
	public static final String AUDIO_MP3 = "audio/mp3";
	public static final String AUDIO_MPEG3 = "audio/mpeg3";
	public static final String AUDIO_MPEG = "audio/mpeg";
	public static final String AUDIO_MPG = "audio/mpg";
	public static final String AUDIO_MP4 = "audio/mp4";
	public static final String AUDIO_X_MID = "audio/x-mid";
	public static final String AUDIO_X_MIDI = "audio/x-midi";
	public static final String AUDIO_X_MP3 = "audio/x-mp3";
	public static final String AUDIO_X_MPEG3 = "audio/x-mpeg3";
	public static final String AUDIO_X_MPEG = "audio/x-mpeg";
	public static final String AUDIO_X_MPG = "audio/x-mpg";
	public static final String AUDIO_3GPP = "audio/3gpp";
	public static final String AUDIO_X_WAV = "audio/x-wav";
	public static final String AUDIO_OGG = "application/ogg";

	public static final String VIDEO_UNSPECIFIED = "video/*";
	public static final String VIDEO_3GPP = "video/3gpp";
	public static final String VIDEO_3G2 = "video/3gpp2";
	public static final String VIDEO_H263 = "video/h263";
	public static final String VIDEO_MP4 = "video/mp4";

	public static final String APP_SMIL = "application/smil";
	public static final String APP_WAP_XHTML = "application/vnd.wap.xhtml+xml";
	public static final String APP_XHTML = "application/xhtml+xml";

	public static final String APP_DRM_CONTENT = "application/vnd.oma.drm.content";
	public static final String APP_DRM_MESSAGE = "application/vnd.oma.drm.message";
}
