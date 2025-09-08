import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PolynomialSolver {

    public static void main(String[] args) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get("input.json")));

        int n = extractIntValue(content, "\"n\"");
        int k = extractIntValue(content, "\"k\"");

        Map<Integer, Root> rootsMap = new HashMap<>();

        for (int i = 1; i <= 20; i++) {
            String key = "\"" + i + "\"";
            int idx = content.indexOf(key);
            if (idx == -1) continue;

            int start = content.indexOf("{", idx);
            int end = findMatchingBrace(content, start);
            if (start == -1 || end == -1) continue;

            String rootJson = content.substring(start, end + 1);

            String baseStr = extractStringValue(rootJson, "\"base\"");
            String valueStr = extractStringValue(rootJson, "\"value\"");

            if (baseStr != null && valueStr != null) {
                rootsMap.put(i, new Root(baseStr, valueStr));
            }
        }

        List<Integer> sortedKeys = new ArrayList<>(rootsMap.keySet());
        Collections.sort(sortedKeys);

        List<BigInteger> roots = new ArrayList<>();
        for (int i = 0; i < sortedKeys.size(); i++) {
            Root r = rootsMap.get(sortedKeys.get(i));
            BigInteger val = new BigInteger(r.value, Integer.parseInt(r.base));
            roots.add(val);
        }

        BigInteger constant = BigInteger.ONE;
        for (int i = 0; i < k && i < roots.size(); i++) {
            constant = constant.multiply(roots.get(i));
        }
        if (k % 2 == 1) {
            constant = constant.negate();
        }

        System.out.println(constant);
    }

    private static int extractIntValue(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1) return -1;
        int colon = json.indexOf(":", idx);
        int start = colon + 1;

        while (start < json.length() && !Character.isDigit(json.charAt(start)) && json.charAt(start) != '-') {
            start++;
        }

        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) {
            end++;
        }

        String numStr = json.substring(start, end);
        return Integer.parseInt(numStr);
    }

    private static String extractStringValue(String json, String key) {
        int idx = json.indexOf(key);
        if (idx == -1) return null;
        int colon = json.indexOf(":", idx);
        int quote1 = json.indexOf("\"", colon);
        int quote2 = json.indexOf("\"", quote1 + 1);
        if (quote1 == -1 || quote2 == -1) return null;
        return json.substring(quote1 + 1, quote2);
    }

    private static int findMatchingBrace(String str, int pos) {
        int count = 0;
        for (int i = pos; i < str.length(); i++) {
            if (str.charAt(i) == '{') count++;
            else if (str.charAt(i) == '}') {
                count--;
                if (count == 0) return i;
            }
        }
        return -1;
    }

    static class Root {
        String base;
        String value;

        Root(String base, String value) {
            this.base = base;
            this.value = value;
        }
    }
}
