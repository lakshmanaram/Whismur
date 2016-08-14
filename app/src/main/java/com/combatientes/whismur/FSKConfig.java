package com.combatientes.whismur;

import android.media.AudioFormat;

import java.io.IOException;

public class FSKConfig {

	public static final int SAMPLE_RATE_44100 = 44100; // LCMx2
	public static final int SAMPLE_RATE_22050 = 22050; // LEAST COMMON MULTIPLE
														// OF 2450, 1225, 630,
														// 315 and 126
	public static final int SAMPLE_RATE_29400 = 29400; // DEFAULT for 1225; 24
														// samples per bit
	public static final int PCM_16BIT = AudioFormat.ENCODING_PCM_16BIT;

	public static final int CHANNELS_MONO = 1;

	public static final int SOFT_MODEM_MODE_4 = 4;
//	public static final int SOFT_MODEM_MODE_4_BAUD_RATE = 1225;
	public static final int SOFT_MODEM_MODE_4_BAUD_RATE = 980;
//	public static final int SOFT_MODEM_MODE_4_LOW_FREQ = 4900;
	public static final int SOFT_MODEM_MODE_4_LOW_FREQ = 4900;
//	public static final int SOFT_MODEM_MODE_4_HIGH_FREQ = 7350;
	public static final int SOFT_MODEM_MODE_4_HIGH_FREQ = 7790;

	public static final int SOFT_MODEM_MODE_5 = 5;
	public static final int SOFT_MODEM_MODE_5_BAUD_RATE = 1470;
	public static final int SOFT_MODEM_MODE_5_LOW_FREQ = 5880;
	public static final int SOFT_MODEM_MODE_5_HIGH_FREQ = 11270;
//	public static final int SOFT_MODEM_MODE_5_BAUD_RATE = 1470;
//	public static final int SOFT_MODEM_MODE_5_LOW_FREQ = 4900;
//	public static final int SOFT_MODEM_MODE_5_HIGH_FREQ = 8820;

	public static final int THRESHOLD_10P = 10; // above and under; sums 20%
	public static final int THRESHOLD_20P = 20; // above and under; sums 40%

	public static final int RMS_SILENCE_THRESHOLD_16BIT = 1000;

	public static final int DECODER_DATA_BUFFER_SIZE = 8;
	public static final int ENCODER_DATA_BUFFER_SIZE = 128;

	public static final int ENCODER_PRE_CARRIER_BITS = 3;
	public static final int ENCODER_POST_CARRIER_BITS = 1;
	public static final int ENCODER_SILENCE_BITS = 3;

	// /

	public int sampleRate;
	public int pcmFormat;
	public int channels;
	public int modemMode;

	// /

	public int samplesPerBit;

	public int modemBaudRate;
	public int modemFreqLow;
	public int modemFreqHigh;

	public int modemFreqLowThresholdHigh;
	public int modemFreqHighThresholdHigh;
//	public int modemFreqLowThresholdLow;
//	public int modemFreqHighThresholdLow;

	public int rmsSilenceThreshold;

	public FSKConfig(int sampleRate, int pcmFormat, int channels,
					 int modemMode, int threshold) throws IOException {
		this.sampleRate = sampleRate;
		this.pcmFormat = pcmFormat;
		this.channels = channels;
		this.modemMode = modemMode;

		switch (modemMode) {

		case SOFT_MODEM_MODE_4:

			this.modemBaudRate = SOFT_MODEM_MODE_4_BAUD_RATE;
			this.modemFreqLow = SOFT_MODEM_MODE_4_LOW_FREQ;
			this.modemFreqHigh = SOFT_MODEM_MODE_4_HIGH_FREQ;

			break;

		case SOFT_MODEM_MODE_5:

			this.modemBaudRate = SOFT_MODEM_MODE_5_BAUD_RATE;
			this.modemFreqLow = SOFT_MODEM_MODE_5_LOW_FREQ;
			this.modemFreqHigh = SOFT_MODEM_MODE_5_HIGH_FREQ;

			break;
		}

		if (this.sampleRate % this.modemBaudRate > 0) {
			// wrong config

			throw new IOException("Invalid sample rate or baudrate");
		}

		this.samplesPerBit = this.sampleRate / this.modemBaudRate;

		this.modemFreqLowThresholdHigh = this.modemFreqLow
				+ Math.round((this.modemFreqLow * threshold) / 100.0f);
		this.modemFreqHighThresholdHigh = this.modemFreqHigh
				+ Math.round((this.modemFreqHigh * threshold) / 100.0f);
		this.rmsSilenceThreshold = FSKConfig.RMS_SILENCE_THRESHOLD_16BIT;
	}

}
