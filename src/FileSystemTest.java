import java.util.*;

public class FileSystemTest {

    public static Folder readFolder(Scanner sc) {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < totalFiles; i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String[] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args) {

        //file reading from input

        Scanner sc = new Scanner(System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());

    }

    static class FileNameExistsException extends Exception {
        public FileNameExistsException(String file, String folder) {
            super(String.format("There is already a file named %s in the folder %s", file, folder));
        }
    }

    interface IFile {
        /// може да се пристапи до неговото име
        String getFileName();

        /// може да се добие неговата големина во long
        long getFileSize();

        /// може да се добие String репрезентација на фајлот
        String getFileInfo(int offset);

        /// може да се сортира датотеката доколку е колекција од датотеки според големините на датотеките кои ги содржи
        void sortBySize();

        /// може да се пресмета големината на најголемата обична датотека во датотеката
        long findLargestFile();
    }

    static class File implements IFile {
        private final String name;
        private final long size;

        File(String name, long size) {
            this.name = name;
            this.size = size;
        }

        @Override
        public String getFileName() {
            return name;
        }

        @Override
        public long getFileSize() {
            return size;
        }

        @Override
        public String getFileInfo(int offset) {
            return "    ".repeat(offset) + String.format("File name: %10s File size: %10d\n", name, size);
        }

        @Override
        public void sortBySize() {
            // alr sorted
        }

        @Override
        public long findLargestFile() {
            return size;
        }
    }

    static class Folder implements IFile {
        private final String name;
        private final List<IFile> fileList;

        Folder(String name) {
            this.name = name;
            fileList = new ArrayList<>();
        }

        void addFile(IFile file) throws FileNameExistsException {
            if (fileList.stream().map(IFile::getFileName).anyMatch(name -> name.equals(file.getFileName())))
                throw new FileNameExistsException(file.getFileName(), name);
            fileList.add(file);
        }

        @Override
        public String getFileName() {
            return name;
        }

        @Override
        public long getFileSize() {
            return fileList.stream().mapToLong(IFile::getFileSize).sum();
        }

        @Override
        public String getFileInfo(int offset) {
            StringBuilder sb = new StringBuilder();

            sb.append("    ".repeat(offset));
            sb.append(String.format("Folder name: %10s Folder size: %10d\n", name, getFileSize()));

            // sortBySize();
            fileList.forEach(f -> sb.append(f.getFileInfo(offset + 1)));

            return sb.toString();
        }

        @Override
        public void sortBySize() {
            fileList.forEach(IFile::sortBySize);
            fileList.sort(Comparator.comparing(IFile::getFileSize));
        }

        @Override
        public long findLargestFile() {
            return fileList.stream().mapToLong(IFile::findLargestFile).max().orElse(0);
        }
    }

    static class FileSystem {
        Folder root;

        FileSystem() {
            root = new Folder("root");
        }

        void addFile(IFile file) throws FileNameExistsException {
            root.addFile(file);
        }

        long findLargestFile() {
            return root.findLargestFile();
        }

        void sortBySize() {
            root.sortBySize();;
        }

        @Override
        public String toString() {
            return root.getFileInfo(0);
        }
    }
}