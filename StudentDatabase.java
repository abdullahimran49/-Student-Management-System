import java.io.*;
import java.util.*;

public class StudentDatabase {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Student> students = new ArrayList<>();
        File file = new File("student_data.txt");

        if (file.exists()) {
            students = loadStudentData(file);
            System.out.println("\nStudent data loaded from file.");
        }

        int choice;
        do {
            printMenu();
            System.out.print("\nEnter your choice: ");
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addStudent(scanner, students);
                case 2 -> displayStudentInformation(scanner, students);
                case 3 -> updateStudentInformation(scanner, students);
                case 4 -> displayAverageGradesAndGPA(scanner, students);
                case 5 -> deleteStudent(scanner, students);
                case 6 -> searchStudent(scanner, students);
                case 7 -> sortStudentsByGPA(students);
                case 8 -> saveStudentData(file, students);
                case 9 -> System.out.println("\nExiting the program. Goodbye!");
                default -> System.out.println("\nInvalid choice. Please enter a valid option.");
            }
        } while (choice != 9);
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n*********** MENU ***********");
        System.out.println("1. Add Student");
        System.out.println("2. Display Student Information");
        System.out.println("3. Update Student Information");
        System.out.println("4. Display Average Grades and GPA");
        System.out.println("5. Delete a Student Record");
        System.out.println("6. Search for a Student");
        System.out.println("7. Sort Students by GPA");
        System.out.println("8. Save Data");
        System.out.println("9. Exit");
    }

    private static void addStudent(Scanner scanner, List<Student> students) {
        System.out.print("\nEnter student name: ");
        String name = scanner.nextLine();

        System.out.print("Enter age: ");
        int age = getValidInt(scanner, 10, 100);

        System.out.print("Enter contact number: ");
        String contact = scanner.nextLine();

        System.out.print("Enter blood group: ");
        String bloodGroup = scanner.nextLine();

        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        int[] grades = new int[6];
        String[] subjects = {"Programming Fundamentals", "Ideology", "Islamic Studies", "English", "ICT", "Linear Algebra"};

        System.out.println("\nEnter grades (out of 100):");
        for (int i = 0; i < 6; i++) {
            System.out.print(subjects[i] + ": ");
            grades[i] = getValidInt(scanner, 0, 100);
        }

        students.add(new Student(name, age, contact, bloodGroup, address, grades));
        System.out.println("\nStudent added successfully.");
    }

    private static void displayStudentInformation(Scanner scanner, List<Student> students) {
        System.out.print("\nEnter student name: ");
        String name = scanner.nextLine();
        students.stream().filter(s -> s.name.equalsIgnoreCase(name)).findFirst()
                .ifPresentOrElse(System.out::println, () -> System.out.println("Student not found!"));
    }

    private static void updateStudentInformation(Scanner scanner, List<Student> students) {
        System.out.print("\nEnter student name to update: ");
        String name = scanner.nextLine();

        for (Student student : students) {
            if (student.name.equalsIgnoreCase(name)) {
                System.out.print("Enter new contact number: ");
                student.contact = scanner.nextLine();

                System.out.print("Enter new address: ");
                student.address = scanner.nextLine();

                System.out.println("\nStudent information updated successfully.");
                return;
            }
        }
        System.out.println("Student not found!");
    }

    private static void displayAverageGradesAndGPA(Scanner scanner, List<Student> students) {
        System.out.print("\nEnter student name: ");
        String name = scanner.nextLine();

        for (Student student : students) {
            if (student.name.equalsIgnoreCase(name)) {
                double average = Arrays.stream(student.grades).average().orElse(0.0);
                double gpa = student.calculateGPA();

                System.out.println("\nAverage Grades for " + name + ": " + average);
                System.out.println("GPA for " + name + ": " + gpa);
                return;
            }
        }
        System.out.println("Student not found!");
    }

    private static void deleteStudent(Scanner scanner, List<Student> students) {
        System.out.print("\nEnter student name to delete: ");
        String name = scanner.nextLine();
        boolean removed = students.removeIf(student -> student.name.equalsIgnoreCase(name));

        if (removed) {
            System.out.println("Student record deleted.");
        } else {
            System.out.println("Student not found!");
        }
    }

    private static void searchStudent(Scanner scanner, List<Student> students) {
        System.out.print("\nEnter student name to search: ");
        displayStudentInformation(scanner, students);
    }

    private static void sortStudentsByGPA(List<Student> students) {
        students.sort(Comparator.comparingDouble(Student::calculateGPA).reversed());
        System.out.println("\nStudents sorted by GPA:");
        students.forEach(System.out::println);
    }

    private static void saveStudentData(File file, List<Student> students) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(students);
            System.out.println("\nStudent data saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private static List<Student> loadStudentData(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Student>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static int getValidInt(Scanner scanner, int min, int max) {
        int value;
        while (true) {
            while (!scanner.hasNextInt()) {
                System.out.println("Invalid input! Enter a number.");
                scanner.next();
            }
            value = scanner.nextInt();
            if (value >= min && value <= max) break;
            System.out.println("Enter a number between " + min + " and " + max);
        }
        scanner.nextLine(); // Consume newline
        return value;
    }
}

class Student implements Serializable {
    String name, contact, bloodGroup, address;
    int age;
    int[] grades;

    public Student(String name, int age, String contact, String bloodGroup, String address, int[] grades) {
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.bloodGroup = bloodGroup;
        this.address = address;
        this.grades = grades;
    }

    public double calculateGPA() {
        return Arrays.stream(grades).average().orElse(0.0) / 25;
    }

    public String toString() {
        return name + " (GPA: " + calculateGPA() + ")";
    }
}
