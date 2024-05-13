import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Teat {
    public static void main(String[] args) {
        TestUse testUse = new TestUse() {
        };
        Type superclass = testUse.getClass().getGenericSuperclass();
        System.out.println(superclass);
        Field[] fields = TestUse.class.getDeclaredFields();
        for (Field f:
             fields) {
            Type type = f.getGenericType();
            System.out.print(f.getName() + ":  ");
            if (type instanceof ParameterizedType) {
                // 参数化类型
                ParameterizedType p = (ParameterizedType) type;
                Type[] targs = p.getActualTypeArguments();
                for (Type t:
                     targs) {
                    System.out.print("  " + t.getTypeName());
                    if (t instanceof TypeVariable) {
                        System.out.println("true");
                    }else if (t instanceof GenericArrayType) {
                        GenericArrayType arrayType = (GenericArrayType) t;
                        System.out.print(arrayType.getGenericComponentType());
                        System.out.println();
                    }else if (t instanceof TypeVariable) {
                        TypeVariable typeVariable = (TypeVariable) t;
                        System.out.print(typeVariable.getName());
                        System.out.println();
                    }else if (t instanceof WildcardType) {
                        WildcardType wildcardType = (WildcardType) t;
                        Type[] upperBounds = wildcardType.getUpperBounds();
                        Type[] lowerBounds = wildcardType.getLowerBounds();
                        System.out.print(Arrays.toString(upperBounds));
                        System.out.print(Arrays.toString(lowerBounds));
                    }
                }
                System.out.println();
            }else if (type instanceof GenericArrayType) {
                GenericArrayType arrayType = (GenericArrayType) type;
                System.out.print(arrayType.getGenericComponentType());
                System.out.println();
            }else if (type instanceof TypeVariable) {
                TypeVariable typeVariable = (TypeVariable) type;
                System.out.print(typeVariable.getName());
                System.out.println();
            }else if (type instanceof WildcardType) {
                WildcardType wildcardType = (WildcardType) type;
                Type[] upperBounds = wildcardType.getUpperBounds();
                Type[] lowerBounds = wildcardType.getLowerBounds();
                System.out.print(Arrays.toString(upperBounds));
                System.out.print(Arrays.toString(lowerBounds));
            }
        }
    }
}
class TestUse{
    public List<String> list1;
    public List<List<String>> list2;
    public Map<Long, String[]> map;
    public List<String>[] array;
    public List<String[]> list3;
    public List<? extends Long> list4;
    public String string;
//    public E e;
    public List<? super Long> list5;
//    public E[] array2;

    public static int num;

    int getNum() {
        return num;
    }

    void setNum(int num) {
        TestUse.num = num;
    }
}
