package example1;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;

import com.sun.nio.file.ExtendedWatchEventModifier;

public class AutoCompileAndReLoad {
	public static void main(String[] args) {
		Path myDir = Paths
				.get("C:/workspace/workspace2/classloader-examples/src");

		registerRecursive(myDir);

	}

	public static void registerRecursive(final Path root) {
		try {
			WatchService watchService = root.getFileSystem().newWatchService();
			// Object standardEventsArray;
			// standardEventsArray ={ StandardWatchEventKinds.ENTRY_CREATE,
			// StandardWatchEventKinds.ENTRY_MODIFY,
			// StandardWatchEventKinds.ENTRY_DELETE};
			Kind<Path>[] standardEventsArray = new Kind[3];
			standardEventsArray[0] = StandardWatchEventKinds.ENTRY_CREATE;
			standardEventsArray[1] = StandardWatchEventKinds.ENTRY_MODIFY;
			standardEventsArray[2] = StandardWatchEventKinds.ENTRY_DELETE;
			root.register(watchService, standardEventsArray,
					ExtendedWatchEventModifier.FILE_TREE);

			// loop forever to watch directory
			while (true) {
				WatchKey watchKey;
				watchKey = watchService.take(); // This call is blocking until
												// events are present

				// Create the list of path files
				ArrayList<String> filesLog = new ArrayList<String>();
				if (root.toFile().exists()) {
					File fList[] = root.toFile().listFiles();
					for (int i = 0; i < fList.length; i++) {
						filesLog.add(fList[i].getName());
					}
				}

				// Poll for file system events on the WatchKey
				for (final WatchEvent<?> event : watchKey.pollEvents()) {
					printEvent(event);
				}

				// Save the log
				saveLog(filesLog);

				if (!watchKey.reset()) {
					System.out.println("Path deleted");
					watchKey.cancel();
					watchService.close();
					break;
				}
			}

		} catch (InterruptedException ex) {
			System.out.println("Directory Watcher Thread interrupted");
			return;
		} catch (IOException ex) {
			ex.printStackTrace(); // Loggin framework
			return;
		}
	}

	private static void saveLog(ArrayList<String> filesLog) {
		System.out.println(filesLog);

	}

	private static void printEvent(WatchEvent<?> event) {
		boolean flag = true;
		if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
			System.out.println("Created: " + event.context().toString());
		}
		if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
			System.out.println("Delete: " + event.context().toString());
		}
		if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY
				&& event.context().toString().contains("java")) {

			if (flag) {
				flag = !flag;
				System.out.println("Modify: " + event.context().toString());
				 try {
					Main.compileAndReload(
					 new MyClassLoader(AutoCompileAndReLoad.class
					 .getClassLoader()), "example1.Greet"
					 ,"-d", "bin" ,"C:/workspace/workspace2/classloader-examples/src/example1/Greet.java");
				}  catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
