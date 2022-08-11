import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.JTextPane;

public class Window {

	public static JFrame frame;
	public static int inventoryWidth = 176;
	public static int inventoryHeight = 166;
	public static boolean hasOverlay = false;
	public static File currentGifInventory;
	public static File currentOverlay;

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, InterruptedException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		frame = new JFrame();
		frame.getContentPane().setBackground(new Color(102, 102, 102));
		frame.setBounds(100, 100, 660, 450);
		frame.setTitle("Swim Services Animated Inventory Tool");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.getContentPane().setLayout(null);

		Icon selectImgIcon = new ImageIcon(Window.class.getResource("select gif.png"));

		JButton gifPreview = new JButton(selectImgIcon);
		gifPreview.setForeground(new Color(0, 255, 0));
		gifPreview.setBackground(new Color(0, 255, 51));
		gifPreview.setBounds(20, 23, 256, 256);
		gifPreview.setVisible(false);

		Icon selectOverlayIcon = new ImageIcon(Window.class.getResource("select overlay.png"));
		JButton overlayPreview = new JButton(selectOverlayIcon);
		overlayPreview.setForeground(Color.GREEN);
		overlayPreview.setBackground(new Color(0, 255, 51));
		overlayPreview.setBounds(370, 23, 256, 256);
		overlayPreview.setVisible(false);

		frame.setVisible(true);

		File exportDest = new File(System.getProperty("user.dir"));
		System.out.println(exportDest.getAbsolutePath());

		JButton buildInvButton = new JButton("Create Animated Inventory MCPACK");
		buildInvButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		buildInvButton.setBounds(20, 289, 256, 70);
		buildInvButton.setVisible(false);

		JCheckBox overlayToggle = new JCheckBox("Include overlay PNG");
		overlayToggle.setBounds(370, 289, 129, 23);

		JSpinner frameSpinner = new JSpinner();
		frameSpinner.setModel(new SpinnerListModel(new String[] { "0.05", "0.06", "0.07", "0.08", "0.09" }));
		frameSpinner.setValue("0.06");
		frameSpinner.setEditor(new JSpinner.DefaultEditor(frameSpinner)); // to disable text editing in the spinner's display field
		frameSpinner.setBounds(453, 319, 46, 20);

		JTextPane disclaimerText = new JTextPane();
		disclaimerText.setEditable(false);
		disclaimerText.setText("MCPE UI can only handle up to 40 frames max so your gif may be cut short once rendered in game. "
				+ "Frame duration can be 0.05-0.09, any faster could cause crashes. Portrait aspect gifs may look weird.");
		disclaimerText.setBounds(370, 345, 256, 60);

		JTextPane labelFrameDuration = new JTextPane();
		labelFrameDuration.setEditable(false);
		labelFrameDuration.setText("Frame Duration:");
		labelFrameDuration.setBounds(370, 319, 85, 20);

		frame.getContentPane().add(overlayToggle);
		frame.getContentPane().add(buildInvButton);
		frame.getContentPane().add(gifPreview);
		frame.getContentPane().add(overlayPreview);
		frame.getContentPane().add(frameSpinner);
		frame.getContentPane().add(labelFrameDuration);
		frame.getContentPane().add(disclaimerText);

		buildInvButton.setVisible(true);
		gifPreview.setVisible(true);
		overlayPreview.setVisible(true);
		frameSpinner.setVisible(true);
		labelFrameDuration.setVisible(true);
		disclaimerText.setVisible(true);

		gifPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					System.out.println("gif preview button clicked");
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop")); // sets current directory
					fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					fileChooser.setAcceptAllFileFilterUsed(false);
					int response = fileChooser.showOpenDialog(null);
					if (response == JFileChooser.APPROVE_OPTION) {
						String chosenFile = fileChooser.getSelectedFile().getAbsolutePath();
						String extension = FilenameUtils.getExtension(chosenFile.toLowerCase());
						if (extension.equals("gif")) {
							ImageIcon image = new ImageIcon(chosenFile);
							image.setImage(image.getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT));
							gifPreview.setIcon(image);
							currentGifInventory = new File(chosenFile);
						} else {
							JOptionPane.showMessageDialog(null, "Must be a Gif File!", "Error!", JOptionPane.ERROR_MESSAGE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		gifPreview.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;
			String currentDraggedFile;

			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					@SuppressWarnings("unchecked")
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					int i = 0;
					for (File currentFile : droppedFiles) {
						System.out.println(currentFile);
						currentDraggedFile = currentFile.getAbsolutePath();
						i++;
					}
					if (i <= 1) {
						String extension = FilenameUtils.getExtension(currentDraggedFile.toLowerCase());
						if (extension.equals("gif")) {
							currentGifInventory = new File(currentDraggedFile);
							ImageIcon image = new ImageIcon(currentDraggedFile);
							image.setImage(image.getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT));
							gifPreview.setIcon(image);
						} else {
							JOptionPane.showMessageDialog(null, "Must be a Gif File!", "Error!", JOptionPane.ERROR_MESSAGE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		overlayPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					System.out.println("overlay preview button clicked");
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setCurrentDirectory(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop")); // sets current directory
					fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					fileChooser.setAcceptAllFileFilterUsed(false);
					int response = fileChooser.showOpenDialog(null);
					if (response == JFileChooser.APPROVE_OPTION) {
						String chosenFile = fileChooser.getSelectedFile().getAbsolutePath();
						String extension = FilenameUtils.getExtension(chosenFile.toLowerCase());
						if (extension.equals("png")) {
							ImageIcon image = new ImageIcon(chosenFile);
							image.setImage(image.getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT));
							overlayPreview.setIcon(image);
							hasOverlay = true;
							currentOverlay = new File(chosenFile);
						} else {
							JOptionPane.showMessageDialog(null, "Must be a PNG File!", "Error!", JOptionPane.ERROR_MESSAGE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		overlayPreview.setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;
			String currentDraggedFile;

			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					@SuppressWarnings("unchecked")
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					int i = 0;
					for (File currentFile : droppedFiles) {
						System.out.println(currentFile);
						currentDraggedFile = currentFile.getAbsolutePath();
						i++;
					}
					if (i <= 1) {
						String extension = FilenameUtils.getExtension(currentDraggedFile.toLowerCase());
						if (extension.equals("png")) {
							ImageIcon image = new ImageIcon(currentDraggedFile);
							image.setImage(image.getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT));
							overlayPreview.setIcon(image);
							hasOverlay = true;
							currentOverlay = new File(currentDraggedFile);
						} else {
							JOptionPane.showMessageDialog(null, "Must be a PNG File!", "Error!", JOptionPane.ERROR_MESSAGE);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		buildInvButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					if (currentGifInventory != null) {
						System.out.println("building inventory");

						int correctedX = 352;
						int correctedY = 332;

						DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd HH_mm_ss");
						LocalDateTime now = LocalDateTime.now();
						System.out.println(dtf.format(now));
						File writePath = new File(exportDest.getAbsolutePath() + "\\animated Inventory " + dtf.format(now) + "\\");
						new File(writePath.getAbsolutePath()).mkdirs();
						try {
							BufferedImage after = null; // empty for now, declared here so I can write it as the packs_icon
							String[] imageatt = new String[] { "imageLeftPosition", "imageTopPosition", "imageWidth", "imageHeight" };
							ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
							ImageInputStream ciis = ImageIO.createImageInputStream(currentGifInventory);
							reader.setInput(ciis, false);
							int frames = reader.getNumImages(true);
							if (frames > 40) {
								frames = 40;
							}
							BufferedImage master = null;
							BufferedImage graph = new BufferedImage(correctedX, correctedY * frames, BufferedImage.TYPE_INT_ARGB);
							Graphics2D inventorySpriteSheet = graph.createGraphics();
							for (int i = 0; i < frames; i++) {
								BufferedImage image = reader.read(i);
								IIOMetadata metadata = reader.getImageMetadata(i);
								Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
								NodeList children = tree.getChildNodes();
								for (int j = 0; j < children.getLength(); j++) {
									Node nodeItem = children.item(j);
									if (nodeItem.getNodeName().equals("ImageDescriptor")) {
										Map<String, Integer> imageAttr = new HashMap<String, Integer>();
										for (int k = 0; k < imageatt.length; k++) {
											NamedNodeMap attr = nodeItem.getAttributes();
											Node attnode = attr.getNamedItem(imageatt[k]);
											imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
										}
										if (i == 0) {
											master = new BufferedImage(imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
										}
										master.getGraphics().drawImage(image, imageAttr.get("imageLeftPosition"), imageAttr.get("imageTopPosition"), null);
									}
								}
								ImageIcon preview = new ImageIcon(master);
								preview.setImage(preview.getImage().getScaledInstance(correctedX, correctedY, Image.SCALE_DEFAULT));
								gifPreview.setIcon(preview);
								frame.repaint();
								Icon icon = gifPreview.getIcon();
								BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
								Graphics g = bi.createGraphics();
								icon.paintIcon(null, g, 0, 0);

								int w = bi.getWidth();
								int h = bi.getHeight();

								after = new BufferedImage(w * (correctedX / correctedY), h * (correctedX / correctedY), BufferedImage.TYPE_INT_ARGB);
								AffineTransform at = new AffineTransform();
								at.scale(correctedX / w, correctedY / h);
								AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
								after = scaleOp.filter(bi, after);

								inventorySpriteSheet.drawImage(after, null, 0, correctedY * i); // add to sprite sheet

								g.dispose();
								ImageIcon reset = new ImageIcon(currentGifInventory.getAbsolutePath());
								reset.setImage(reset.getImage().getScaledInstance(256, 256, Image.SCALE_DEFAULT));
								gifPreview.setIcon(reset);
							}
							new File(writePath.getAbsolutePath() + "\\textures\\animated_ui\\inventory_bg").mkdirs();
							if (overlayToggle.isSelected() && hasOverlay == true) {
								BufferedImage overlay = ImageIO.read(currentOverlay);
								BufferedImage resizedOverlay = resizeImage(overlay, correctedX, correctedY);
								ImageIO.write(resizedOverlay, "png", new File(writePath.getAbsolutePath() + "\\textures\\animated_ui\\inventory_bg\\inventory_overlay.png"));
							} else {
								MCPE.loadAsset(writePath.getAbsolutePath() + "\\textures\\animated_ui\\inventory_bg\\inventory_overlay.png", "/inventory_overlay.png");
							}
							File inventory = new File(writePath.getAbsolutePath() + "\\textures\\animated_ui\\inventory_bg\\inventory_vertical_flipbook.png");
							ImageIO.write(graph, "PNG", inventory);
							File packIcon = new File(writePath.getAbsolutePath() + "\\pack_icon.png"); // thumbnail for pack_icon is last frame of gif
							ImageIO.write(after, "PNG", packIcon);
							String frameDuration = frameSpinner.getValue().toString();
							MCPE.createPack(writePath.getAbsolutePath(), frames, frameDuration, FilenameUtils.removeExtension(currentGifInventory.getName()));
						} catch (IOException e) {
							JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});

	}

	private static BufferedImage resizeImage(BufferedImage original, int width, int height) {
		try {
			BufferedImage resized = new BufferedImage(width, height, original.getType());
			Graphics2D g2 = resized.createGraphics();
			g2.drawImage(original, 0, 0, width, height, null);
			g2.dispose();
			return resized;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		throw new AssertionError("resizeImage function end");
	}
}
