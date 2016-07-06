package com.shell.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * @author shell
 * 二维码工具
 */
public class QRCodeUtil {
	// 二维码包含图片的像素宽度
	private static final int NESTED_IMAGE_WIDTH = 80;

	// 二维码包含图片的像素高度
	private static final int NESTED_IMAGE_HEIGHT = 80;

	// 二维码包含图片的宽度的一半
	private static final int NESTED_IMAGE_HALF_WIDTH = NESTED_IMAGE_WIDTH / 2;

	// 二维码包含图片时，图片的外边距
	private static final int NESTED_IMAGE_MARGIN = 0;

	// 二维码内边距(会占4个像素)
	private static final int MATRIX_MARGIN = 1;

	// 二维码的像素宽度
	private static final int MATRIX_WIDTH = 350;

	// 二维码的像素高度
	private static final int MATRIX_HEIGHT = 350;

	// 二维码的宽度的一半
	private static final int MATRIX_HALF_WIDTH = MATRIX_WIDTH / 2;

	// 二维码的高度的一半
	private static final int MATRIX_HALF_HEIGHT = MATRIX_HEIGHT / 2;

	// 二维码包含信息的编码方式
	private static final String CODE_CHARACTER_SET = "utf-8";

	// 生成的图片文件的格式
	private static final String FILE_EXTENDS = "png";

	/**
	 * 将二维码图片保存
	 * 
	 * @param content
	 *            二维码包含的信息
	 * @param width
	 *            二维码的宽度
	 * @param height
	 *            二维码的高度
	 * @param srcImage
	 *            二维码的包含的图片文件
	 * @param destImage
	 *            生成的二维码图片的保存文件
	 */
	public void encode(String content, File destImage) {
		try {
			BufferedImage bufferedImage = createQrCode(content, null,null);
			ImageIO.write(bufferedImage, FILE_EXTENDS, destImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/** 
	 * 初始化生成二维码参数 
	 */
	private static Map<EncodeHintType, Object> initHint() {
		Map<EncodeHintType, Object> hint = new HashMap<EncodeHintType, Object>();
		hint.put(EncodeHintType.CHARACTER_SET, CODE_CHARACTER_SET);
		hint.put(EncodeHintType.MARGIN, MATRIX_MARGIN);
		hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		
		return hint;
	}
	
	public static BufferedImage createQrCode(String content) {
		return createQrCode(content, null, null);
	}

	/**
	 * 生成二维码图片
	 * 
	 * @param content
	 *            二维码图片所要携带的信息
	 * @param width
	 *            二维码图片的宽度
	 * @param height
	 *            二维码图片的高度
	 * @param srcImage
	 *            放在二维码中的图片文件
	 * @return
	 * @throws WriterException
	 * @throws IOException
	 */
	public static BufferedImage createQrCode(String content, String path, Color color) {

		BufferedImage srcImage = null;
		int[][] srcPixels = null;
		BitMatrix matrix = null;
		
		
		if (path != null && path.trim().length() != 0 && !new File(path).isDirectory() && new File(path).exists()) {
			srcImage = scale(path, NESTED_IMAGE_WIDTH,
					NESTED_IMAGE_HEIGHT, true);
			
			// 如果二维码有包含图片，读取图片的颜色值
			int width = srcImage.getWidth();
			int height = srcImage.getHeight();
			srcPixels = new int[width][height];
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					srcPixels[i][j] = srcImage.getRGB(i, j);
				}
			}
		}
		
		if (color == null) {
			color = Color.black;
		}
		
		// 生成二维码矩阵
		MultiFormatWriter mutiWriter = new MultiFormatWriter();
		Map<EncodeHintType, Object> hint = initHint();
		try {
			matrix = mutiWriter.encode(content, BarcodeFormat.QR_CODE,
					MATRIX_WIDTH, MATRIX_HEIGHT, hint);
		} catch (WriterException e) {
			e.printStackTrace();
		}

		int[] pixels = new int[MATRIX_WIDTH * MATRIX_HEIGHT];

		int nestImageLeftX = MATRIX_HALF_WIDTH - NESTED_IMAGE_HALF_WIDTH;
		int nestImageRightX = MATRIX_HALF_WIDTH + NESTED_IMAGE_HALF_WIDTH;
		int nestImageBottomY = MATRIX_HALF_HEIGHT - NESTED_IMAGE_HALF_WIDTH;
		int nestImageTopY = MATRIX_HALF_HEIGHT + NESTED_IMAGE_HALF_WIDTH;

		int nestImageMarginLeftX = nestImageLeftX - NESTED_IMAGE_MARGIN;
		int nestImageMarginRightX = nestImageRightX + NESTED_IMAGE_MARGIN;
		int nestImageMarginBottomY = nestImageBottomY - NESTED_IMAGE_MARGIN;
		int nestImageMarginTopY = nestImageTopY + NESTED_IMAGE_MARGIN;

		for (int y = 0; y < MATRIX_HEIGHT; y++) {
			for (int x = 0; x < MATRIX_WIDTH; x++) {
				if (srcImage != null) {
					if (x > nestImageLeftX && x < nestImageRightX
							&& y > nestImageBottomY && y < nestImageTopY) {
						pixels[y * MATRIX_WIDTH + x] = srcPixels[x
								- nestImageLeftX][y - nestImageBottomY];
					} else if ((y >= nestImageMarginBottomY && y <= nestImageMarginTopY)
							&& ((x >= nestImageMarginLeftX && x <= nestImageLeftX) || (x >= nestImageRightX && x <= nestImageMarginRightX))) {
						pixels[y * MATRIX_WIDTH + x] = Color.white.getRGB();
					} else if ((x >= nestImageLeftX && x <= nestImageRightX)
							&& ((y >= nestImageMarginBottomY && y <= nestImageBottomY) || (y >= nestImageTopY && y <= nestImageMarginTopY))) {
						pixels[y * MATRIX_WIDTH + x] = Color.white.getRGB();
					} else {
						pixels[y * MATRIX_WIDTH + x] = matrix.get(x, y) ? color
								.getRGB() : Color.white.getRGB();
					}
				} else {
					pixels[y * MATRIX_WIDTH + x] = matrix.get(x, y) ? color
							.getRGB() : Color.white.getRGB();
				}
			}
		}

		BufferedImage image = new BufferedImage(MATRIX_WIDTH, MATRIX_HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		image.getRaster().setDataElements(0, 0, MATRIX_WIDTH, MATRIX_HEIGHT,
				pixels);
		return image;
	}


	/**
	 * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
	 * 
	 * @param srcImageFile
	 *            源文件地址
	 * @param height
	 *            目标高度
	 * @param width
	 *            目标宽度
	 * @param hasFiller
	 *            比例不对时是否需要补白：true为补白; false为不补白;
	 * @throws IOException
	 */
	private static BufferedImage scale(String path, int width,
			int height, boolean hasFiller) {
		
		double ratio = 0.0;
		BufferedImage srcImage = null;
		try {
			srcImage = ImageIO.read(new FileInputStream(new File(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		int srcImageHeight = srcImage.getHeight();
		int srcImageWidth = srcImage.getWidth();
		Image destImage = srcImage.getScaledInstance(width, height,
				BufferedImage.SCALE_SMOOTH);
		if (srcImageHeight > height || srcImageWidth > width) {
			if (srcImageHeight > width) {
				ratio = (double) height / srcImageHeight;
			} else {
				ratio = (double) width / srcImageWidth;
			}
			AffineTransformOp op = new AffineTransformOp(
					AffineTransform.getScaleInstance(ratio, ratio), null);
			destImage = op.filter(srcImage, null);
		}
		if (hasFiller) {
			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D graphic = image.createGraphics();
			graphic.setColor(Color.white);
			graphic.fillRect(0, 0, width, height);
			graphic.drawImage(destImage,
					(width - destImage.getWidth(null)) / 2,
					(height - destImage.getHeight(null)) / 2,
					destImage.getWidth(null), destImage.getHeight(null),
					Color.white, null);
			graphic.dispose();
			destImage = image;
		}
		return (BufferedImage) destImage;
	}

}
