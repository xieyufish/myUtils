package com.shell.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
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
 * @author shell 二维码工具
 */
public class QRCodeUtil {

	private int width;
	private int height;
	private String imageType;
	private Color color;
	private int nestImageWidth;
	private int nestImageHeight;
	private int nestImagePadding;
	
	public int getWidth() {
	
		return width;
	}

	
	public void setWidth(int width) {
	
		this.width = width;
	}

	
	public int getHeight() {
	
		return height;
	}

	
	public void setHeight(int height) {
	
		this.height = height;
	}

	
	public String getImageType() {
	
		return imageType;
	}

	
	public void setImageType(String imageType) {
	
		this.imageType = imageType;
	}

	
	public Color getColor() {
	
		return color;
	}

	
	public void setColor(Color color) {
	
		this.color = color;
	}

	
	public int getNestImageWidth() {
	
		return nestImageWidth;
	}

	
	public void setNestImageWidth(int nestImageWidth) {
	
		this.nestImageWidth = nestImageWidth;
	}

	
	public int getNestImageHeight() {
	
		return nestImageHeight;
	}

	
	public void setNestImageHeight(int nestImageHeight) {
	
		this.nestImageHeight = nestImageHeight;
	}

	
	public int getNestImagePadding() {
	
		return nestImagePadding;
	}

	
	public void setNestImagePadding(int nestImagePadding) {
	
		this.nestImagePadding = nestImagePadding;
	}

	
	public static Map<EncodeHintType, Object> getHint() {
	
		return hint;
	}

	/**
	 * 二维码点阵的生成配置
	 */
	private static final Map<EncodeHintType, Object> hint = new HashMap<>();

	static {
		hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hint.put(EncodeHintType.MARGIN, 1);
		hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
	}
	
	/**
	 * 创建配置颜色的二维码图片
	 * @param info
	 * @return
	 * @throws WriterException
	 */
	public Image create(String info) throws WriterException {

		return create(info, this.color);
	}
	
	/**
	 * 创建指定颜色的二维码图片
	 * @param info
	 * @param color
	 * @return
	 * @throws WriterException
	 */
	public Image create(String info, Color color) throws WriterException {
		
		if (color == null) {
			color = this.color;
		}
		// 生成二维码矩阵
		MultiFormatWriter mutiWriter = new MultiFormatWriter();
		BitMatrix matrix = mutiWriter.encode(info, BarcodeFormat.QR_CODE, this.width,
				this.height, hint);

		int[] pixels = new int[this.width * this.height];

		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				pixels[y * this.width + x] = matrix.get(x, y) ? color.getRGB()
						: Color.WHITE.getRGB();
			}
		}

		BufferedImage image = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_RGB);
		image.getRaster().setDataElements(0, 0, this.width, this.height, pixels);
		return image;
	}
	
	private int[][] getPixels(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int[][] srcPixels = new int[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				srcPixels[i][j] = image.getRGB(i, j);
			}
		}
		return srcPixels;
	}
	
	/**
	 * 创建包含图片的二维码
	 * @param info
	 * @param nestImageFile 图片文件
	 * @return
	 * @throws WriterException 
	 * @throws IOException 
	 */
	public Image create(String info, File nestImageFile) throws WriterException, IOException {
		
		if (nestImageFile == null) {
			return create(info);
		}
		
		BufferedImage nestImage = ImageIO.read(nestImageFile);
		BufferedImage srcImage = scale(nestImage, this.nestImageWidth, this.nestImageHeight, true);
		int[][] srcPixels = getPixels(srcImage);

		// 生成二维码矩阵
		BitMatrix matrix = new MultiFormatWriter().encode(info, BarcodeFormat.QR_CODE, this.width,
					this.height, hint);

		int[] pixels = new int[this.width * this.height];

		int nestImageLeftX = (this.width - this.nestImageWidth) / 2;
		int nestImageRightX = nestImageLeftX + this.nestImageWidth;
		int nestImageBottomY = (this.height - this.nestImageHeight) / 2;
		int nestImageTopY = nestImageBottomY + this.nestImageHeight;

		int nestImageMarginLeftX = nestImageLeftX - this.nestImagePadding;
		int nestImageMarginRightX = nestImageRightX + this.nestImagePadding;
		int nestImageMarginBottomY = nestImageBottomY - this.nestImagePadding;
		int nestImageMarginTopY = nestImageTopY + this.nestImagePadding;

		for (int y = 0; y < this.height; y++) {
			for (int x = 0; x < this.width; x++) {
				if (x > nestImageLeftX && x < nestImageRightX && y > nestImageBottomY
						&& y < nestImageTopY) {
					pixels[y * this.width + x] = srcPixels[x - nestImageLeftX][y
							- nestImageBottomY];
				} else if ((y >= nestImageMarginBottomY && y <= nestImageMarginTopY)
						&& ((x >= nestImageMarginLeftX && x <= nestImageLeftX)
								|| (x >= nestImageRightX
										&& x <= nestImageMarginRightX))) {
					pixels[y * this.width + x] = Color.white.getRGB();
				} else if ((x >= nestImageLeftX && x <= nestImageRightX)
						&& ((y >= nestImageMarginBottomY && y <= nestImageBottomY)
								|| (y >= nestImageTopY
										&& y <= nestImageMarginTopY))) {
					pixels[y * this.width + x] = Color.white.getRGB();
				} else {
					pixels[y * this.width + x] = matrix.get(x, y) ? this.color.getRGB()
							: Color.white.getRGB();
				}
			}
		}

		BufferedImage image = new BufferedImage(this.width, this.height,
				BufferedImage.TYPE_INT_RGB);
		image.getRaster().setDataElements(0, 0, this.width, this.height, pixels);
		return image;
	}

	/**
	 * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
	 * 
	 * @param srcImageFile 源文件地址
	 * @param height 目标高度
	 * @param width 目标宽度
	 * @param hasFiller 比例不对时是否需要补白：true为补白; false为不补白;
	 * @throws IOException
	 */
	private static BufferedImage scale(BufferedImage srcImage, int width, int height,
			boolean hasFiller) {

		double ratio = 0.0;

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
			graphic.drawImage(destImage, (width - destImage.getWidth(null)) / 2,
					(height - destImage.getHeight(null)) / 2, destImage.getWidth(null),
					destImage.getHeight(null), Color.white, null);
			graphic.dispose();
			destImage = image;
		}
		return (BufferedImage) destImage;
	}

}
