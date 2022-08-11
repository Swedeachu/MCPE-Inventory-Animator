import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

public class MCPE {

	public static void createPack(String pack, int frames, String frameDuration, String name) throws IOException {
		createManifest(pack, name);
		createCommonCell(pack);
		createUIDX(pack);
		createUI(pack, frames, frameDuration);
		packagePack(pack, name);
	}

	public static void createManifest(String pack, String name) throws IOException {
		try {
			FileWriter manifest = new FileWriter(pack + "\\manifest.json");

			String UUID = UUIDGenerator.generateType1UUID().toString();
			String UUID2 = UUIDGenerator.generateType1UUID().toString();

			String description = "Swim Services Animated Inventory Tool\nBy Swimfan72, CrisXolt, and Polrflare\ndiscord.gg/swim";

			manifest.write("{\n");
			manifest.write("    \"format_version\": 1,\n");
			manifest.write("    \"header\": {\n");
			manifest.write("        \"description\": \"" + description + "\",\n");
			manifest.write("        \"name\": \"" + name + "\",\n");
			manifest.write("        \"uuid\": \"" + UUID + "\",\n");
			manifest.write("        \"version\": [1, 0, 0],\n");
			manifest.write("        \"min_engine_version\": [1, 12, 1]\n");
			manifest.write("    },\n");
			manifest.write("    \"modules\": [\n");
			manifest.write("        {\n");
			manifest.write("            \"description\": \"" + description + "\",\n");
			manifest.write("            \"type\": \"resources\",\n");
			manifest.write("            \"uuid\": \"" + UUID2 + "\",\n");
			manifest.write("            \"version\": [1, 0, 0]\n");
			manifest.write("        }\n");
			manifest.write("    ]\n");
			manifest.write("}\n");

			manifest.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static void createCommonCell(String pack) throws IOException {
		try {
			File cellDir = new File(pack + "//textures//animated_ui//common");
			cellDir.mkdirs();
			loadAsset(cellDir + "//inventory_cell_image_red.json", "/inventory_cell_image_red.json");
			loadAsset(cellDir + "//inventory_cell_image_red.png", "/inventory_cell_image_red.png");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static void createUIDX(String pack) throws IOException {
		try {
			File uidxDir = new File(pack + "//uidx");
			uidxDir.mkdirs();
			File animated_ui_dir = new File(uidxDir + "//animated_ui");
			animated_ui_dir.mkdir();
			File inventoryScreensDir = new File(uidxDir + "//inventory_screens");
			inventoryScreensDir.mkdir();
			loadAsset(animated_ui_dir + "//custom_anim_inventory_bg.uidx", "/uidx/animated_ui/custom_anim_inventory_bg.uidx");
			loadAsset(animated_ui_dir + "//inventory_bg_base.uidx", "/uidx/animated_ui/inventory_bg_base.uidx");
			loadAsset(animated_ui_dir + "//modified_inventory_screen.uidx", "/uidx/animated_ui/modified_inventory_screen.uidx");
			loadAsset(inventoryScreensDir + "//inventory_new_screen.uidx", "/uidx/inventory_screens/inventory_new_screen.uidx");
			loadAsset(inventoryScreensDir + "//inventory_old_screen.uidx", "/uidx/inventory_screens/inventory_old_screen.uidx");
			loadAsset(inventoryScreensDir + "//inventory_screen.uidx", "/uidx/inventory_screens/inventory_screen.uidx");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static void createUI(String pack, int frames, String frameDuration) throws IOException {
		try {
			File ui = new File(pack + "//ui");
			ui.mkdirs();
			loadAsset(ui + "//_global_variables.json", "/ui/_global_variables.json");
			loadAsset(ui + "//_ui_defs.json", "/ui/_ui_defs.json");
			loadAsset(ui + "//inventory_screen.json", "/ui/inventory_screen.json");
			Path global_variables = Paths.get(ui + "\\_global_variables.json");
			Charset charset = StandardCharsets.UTF_8;
			String content = new String(Files.readAllBytes(global_variables), charset);
			String framesString = String.valueOf(frames);
			if (frames < 10) {
				framesString = "0" + framesString;
			} else if (frames > 40) {
				framesString = "40";
			}
			content = content.replaceAll("num", framesString);
			content = content.replaceAll("fum_frames", frameDuration);
			Files.write(global_variables, content.getBytes(charset));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static void packagePack(String path, String name) throws IOException {
		try {
			File pack = new File(path);
			compressMCPACK(pack.getAbsolutePath()); // this copies the file to a zip
			File zippedPack = new File(pack.getAbsolutePath() + ".zip");
			zippedPack.renameTo(new File(pack.getParentFile() + "\\" + name + ".mcpack"));
			FileUtils.forceDelete(pack);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static void compressMCPACK(String dirPath) throws IOException {
		final Path sourceDir = Paths.get(dirPath);
		String zipFileName = dirPath.concat(".zip");
		try {
			final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFileName));
			Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
					try {
						Path targetFile = sourceDir.relativize(file);
						outputStream.putNextEntry(new ZipEntry(targetFile.toString()));
						byte[] bytes = Files.readAllBytes(file);
						outputStream.write(bytes, 0, bytes.length);
						outputStream.closeEntry();
					} catch (IOException e) {
						System.out.println("file compression error");
					}
					return FileVisitResult.CONTINUE;
				}
			});
			outputStream.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static void loadAsset(String path, String asset) {
		try {
			URL inputUrl = MCPE.class.getResource(asset);
			File dest = new File(path);
			FileUtils.copyURLToFile(inputUrl, dest);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	public static class UUIDGenerator {
		public static UUID generateType1UUID() {
			long most64SigBits = get64MostSignificantBitsForVersion1();
			long least64SigBits = get64LeastSignificantBitsForVersion1();
			return new UUID(most64SigBits, least64SigBits);
		}

		private static long get64LeastSignificantBitsForVersion1() {
			Random random = new Random();
			long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
			long variant3BitFlag = 0x8000000000000000L;
			return random63BitLong + variant3BitFlag;
		}

		private static long get64MostSignificantBitsForVersion1() {
			LocalDateTime start = LocalDateTime.of(1582, 10, 15, 0, 0, 0);
			Duration duration = Duration.between(start, LocalDateTime.now());
			long seconds = duration.getSeconds();
			long nanos = duration.getNano();
			long timeForUuidIn100Nanos = seconds * 10000000 + nanos * 100;
			long least12SignificatBitOfTime = (timeForUuidIn100Nanos & 0x000000000000FFFFL) >> 4;
			long version = 1 << 12;
			return (timeForUuidIn100Nanos & 0xFFFFFFFFFFFF0000L) + version + least12SignificatBitOfTime;
		}
	}

}
