import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Unit1 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Unit 1: Text Analysis Tool ===");
        System.out.println();

        // 1. Get text input from the user.
        System.out.println("Enter a paragraph or lengthy text:");
        String text = scanner.nextLine().trim();

        // Input validation for the paragraph.
        while (text.isEmpty()) {
            System.out.println("The text cannot be empty.");
            System.out.println("Please enter a paragraph or lengthy text:");
            text = scanner.nextLine().trim();
        }

        // 2. Calculate the total number of characters.
        int characterCount = text.length();

        // 3. Calculate the total number of words.
        String[] words = text.split("\\s+");
        int wordCount = words.length;

        // 4. Find the most common character.
        Map<Character, Integer> characterFrequency = new HashMap<>();

        for (char character : text.toLowerCase().toCharArray()) {
            if (!Character.isWhitespace(character)) {
                characterFrequency.put(
                    character,
                    characterFrequency.getOrDefault(character, 0) + 1
                );
            }
        }

        char mostCommonCharacter = ' ';
        int highestFrequency = 0;

        for (Map.Entry<Character, Integer> entry : characterFrequency.entrySet()) {
            if (entry.getValue() > highestFrequency) {
                mostCommonCharacter = entry.getKey();
                highestFrequency = entry.getValue();
            }
        }

        // Display the basic analysis.
        System.out.println();
        System.out.println("--- Text Analysis Results ---");
        System.out.println("Total characters: " + characterCount);
        System.out.println("Total words: " + wordCount);
        System.out.println(
            "Most common character: '" + mostCommonCharacter
            + "' (" + highestFrequency + " occurrences)"
        );

        // 5. Ask for a character and find its frequency.
        System.out.println();
        System.out.print("Enter a character to find its frequency: ");
        String characterInput = scanner.nextLine().trim();

        while (characterInput.length() != 1) {
            System.out.println("Please enter exactly one character.");
            System.out.print("Enter a character to find its frequency: ");
            characterInput = scanner.nextLine().trim();
        }

        char searchCharacter = Character.toLowerCase(characterInput.charAt(0));
        int selectedCharacterFrequency = 0;

        for (char character : text.toLowerCase().toCharArray()) {
            if (character == searchCharacter) {
                selectedCharacterFrequency++;
            }
        }

        System.out.println(
            "Frequency of '" + searchCharacter + "': "
            + selectedCharacterFrequency
        );

        // 6. Ask for a word and find its frequency.
        System.out.println();
        System.out.print("Enter a word to find its frequency: ");
        String searchWord = scanner.nextLine().trim().toLowerCase();

        while (searchWord.isEmpty() || searchWord.contains(" ")) {
            System.out.println("Please enter one word only.");
            System.out.print("Enter a word to find its frequency: ");
            searchWord = scanner.nextLine().trim().toLowerCase();
        }

        int wordFrequency = 0;

        for (String word : words) {
            if (word.equalsIgnoreCase(searchWord)) {
                wordFrequency++;
            }
        }

        System.out.println(
            "Frequency of \"" + searchWord + "\": " + wordFrequency
        );

        // 7. Calculate the number of unique words.
        Set<String> uniqueWords = new HashSet<>();

        for (String word : words) {
            uniqueWords.add(word.toLowerCase());
        }

        System.out.println("Number of unique words: " + uniqueWords.size());

        System.out.println();
        System.out.println("=== Analysis Complete ===");

        scanner.close();
    }
}
