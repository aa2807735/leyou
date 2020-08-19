import java.util.Stack;

/**
 * ClassName: test <br/>
 * Description: TODO
 * Date 2020/5/10 20:59
 *
 * @author Lenovo
 **/
public class test {
    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (char chars : s.toCharArray()) {
            if (chars=='(')stack.push(')');
            else if (chars=='{')stack.push('}');
            else if (chars=='[')stack.push(']');
            else if (stack.isEmpty() ||stack.pop()!=chars) return false;
        }
        return stack.isEmpty();
    }

    public static void main(String[] args) {
        System.out.println(isValid("({[]})[][{}]"));
    }
}
