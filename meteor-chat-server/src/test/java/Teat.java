public class Teat {
    public static void main(String[] args) {
        TestUse testUse1 = new TestUse();
        System.out.println(testUse1.getNum());
        TestUse testUse2 = new TestUse();
        testUse2.setNum(10);
        System.out.println(testUse1.getNum());

    }
}
class TestUse{
    public static int num;

    int getNum() {
        return num;
    }

    void setNum(int num) {
        TestUse.num = num;
    }
}
