//package com.go.util;
//
//import java.util.ArrayList;
//import java.util.Random;
//
//public class SortUitlTest {
//    /** 
//     * 排序测试类 
//     *  
//     * 排序算法的分类如下： 1.插入排序（直接插入排序、折半插入排序、希尔排序）； 2.交换排序（冒泡泡排序、快速排序）； 
//     * 3.选择排序（直接选择排序、堆排序）； 4.归并排序； 5.基数排序。 
//     *  
//     * 关于排序方法的选择： (1)若n较小(如n≤50)，可采用直接插入或直接选择排序。 
//     * 当记录规模较小时，直接插入排序较好；否则因为直接选择移动的记录数少于直接插人，应选直接选择排序为宜。 
//     * (2)若文件初始状态基本有序(指正序)，则应选用直接插人、冒泡或随机的快速排序为宜； 
//     * (3)若n较大，则应采用时间复杂度为O(nlgn)的排序方法：快速排序、堆排序或归并排序。 
//     * 
//   * 冒泡法： 这是最原始，也是众所周知的最慢的算法了。他的名字的由来因为它的工作看来象是冒泡： 
//     */  
//    /** 
//     * @description JAVA排序汇总 
//     */  
////    public static ArrayList<ICompareable> createArray() {  
////        Random random = new Random();  
////        int[] array = new int[10];  
////        for (int i = 0; i < 10; i++) {  
////            array[i] = random.nextInt(100) - random.nextInt(100);// 生成两个随机数相减，保证生成的数中有负数  
////        }  
////        System.out.println("==========原始序列==========");  
////        printArray(array);  
////        return array;  
////    }  
//    /** 
//     * @description 打印出随机数 
//     * @date Nov 19, 2009 
//     * @author HDS 
//     * @param data 
//     */  
//    public static void printArray(ArrayList<ICompareable> data) {  
//        for (ICompareable i : data) {  
//            System.out.print(i + " ");  
//        }  
//        System.out.println();  
//    }  
//    
//    /** 
//     * @description 交换相邻两个数 
//     * @date Nov 19, 2009 
//     * @author HDS 
//     * @param data 
//     * @param x 
//     * @param y 
//     */  
//    public static void swap(ArrayList<ICompareable> data, int x, int y) {  
//    	ICompareable temp = data.get(x);  
//        data.set(x, data.get(y));  
//        data.set(y, temp);  
//    }  
//    
//    /** 
//     * 冒泡排序----交换排序的一种 
//     * 方法：相邻两元素进行比较，如有需要则进行交换，每完成一次循环就将最大元素排在最后（如从小到大排序），下一次循环是将其他的数进行类似操作。 
//     * 性能：比较次数O(n^2),n^2/2；交换次数O(n^2),n^2/4 
//     *  
//     * @param data 
//     *            要排序的数组 
//     * @param sortType 
//     *            排序类型 
//     * @return 
//     */  
//    public static void bubbleSort(ArrayList<ICompareable> data, String sortType) {  
//        if (sortType.equals("asc")) { // 正排序，从小排到大  
//            // 比较的轮数  
//            for (int i = 1; i < data.size(); i++) { // 数组有多长,轮数就有多长  
//                // 将相邻两个数进行比较，较大的数往后冒泡  
//                for (int j = 0; j < data.size() - i; j++) {// 每一轮下来会将比较的次数减少
//                    if (data.get(j).getCompareValue() > data.get(j + 1).getCompareValue()) {  
//                        // 交换相邻两个数  
//                        swap(data, j, j + 1);  
//                    }  
//                }  
//            }  
//        } else if (sortType.equals("desc")) { // 倒排序，从大排到小  
//            // 比较的轮数  
//            for (int i = 1; i < data.size(); i++) {  
//                // 将相邻两个数进行比较，较大的数往后冒泡  
//                for (int j = 0; j < data.size() - i; j++) {  
//                    if (data.get(j).getCompareValue() < data.get(j + 1).getCompareValue()) {  
//                        // 交换相邻两个数  
//                        swap(data, j, j + 1);  
//                    }  
//                }  
//            }  
//        } else {  
//            System.out.println("您输入的排序类型错误！");  
//        }  
//        printArray(data);// 输出冒泡排序后的数组值  
//    }  
//    
//    /** 
//     * 直接选择排序法----选择排序的一种 方法：每一趟从待排序的数据元素中选出最小（或最大）的一个元素， 
//     * 顺序放在已排好序的数列的最后，直到全部待排序的数据元素排完。 性能：比较次数O(n^2),n^2/2 交换次数O(n),n 
//     * 交换次数比冒泡排序少多了，由于交换所需CPU时间比比较所需的CUP时间多，所以选择排序比冒泡排序快。 
//     * 但是N比较大时，比较所需的CPU时间占主要地位，所以这时的性能和冒泡排序差不太多，但毫无疑问肯定要快些。 
//     *  
//     * @param data 
//     *            要排序的数组 
//     * @param sortType 
//     *            
//   * 选择法，这种方法提高了一点性能（某些情况下）  这种方法类似我们人为的排序习惯：从数据中选择最小的同第一个值交换，在从省下的部分中  选择最小的与第二个交换，这样往复下去。 
//     * @return 
//     */  
//    public static void selectSort(ArrayList<ICompareable> data, String sortType) {  
//        if (sortType.endsWith("asc")) {// 正排序，从小排到大  
//            int index;  
//            for (int i = 1; i < data.size(); i++) {  
//                index = 0;  
//                for (int j = 1; j <= data.size() - i; j++) {  
//                    if (data.get(j).getCompareValue() > data.get(index).getCompareValue()) {  
//                        index = j;  
//                    }  
//                }  
//                // 交换在位置data.length-i和index(最大值)两个数
//                //第一轮比较出来的最大值应该是放到最后的data.length-1，第二轮比较出的最大值放到data.length-2
//                swap(data, data.size() - i, index);  
//            }  
//        } else if (sortType.equals("desc")) { // 倒排序，从大排到小  
//            int index;  
//            for (int i = 1; i < data.size(); i++) {  
//                index = 0;  
//                for (int j = 1; j <= data.size() - i; j++) {  
//                    if (data.get(j).getCompareValue() < data.get(index).getCompareValue()) {  
//                        index = j;  
//                    }  
//                }  
//                // 交换在位置data.length-i和index(最大值)两个数  
//                swap(data, data.size() - i, index);  
//            }  
//        } else {  
//            System.out.println("您输入的排序类型错误！");  
//        }  
//        printArray(data);// 输出直接选择排序后的数组值  
//    }  
//    
//    /** 
//     * 插入排序 方法：将一个记录插入到已排好序的有序表（有可能是空表）中,从而得到一个新的记录数增1的有序表。 性能：比较次数O(n^2),n^2/4 
//     * 复制次数O(n),n^2/4 比较次数是前两者的一般，而复制所需的CPU时间较交换少，所以性能上比冒泡排序提高一倍多，而比选择排序也要快。 
//     *  
//     * @param data 
//     *           
//    插入法较为复杂，它的基本工作原理是抽出牌，在前面的牌中寻找相应的位置插入，然后继续下一张
//     * @param sortType 
//     *            排序类型 
//     */  
//    public static void insertSort(ArrayList<ICompareable> data, String sortType) {  
//        if (sortType.equals("asc")) { // 正排序，从小排到大  
//            // 比较的轮数  
//            for (int i = 1; i < data.size(); i++) {  
//                // 保证前i+1个数排好序  
//                for (int j = 0; j < i; j++) { 
//                    if (data.get(j).getCompareValue() > data.get(i).getCompareValue()) {  
//                        // 交换在位置j和i两个数  
//                        swap(data, i, j);  
//                    }  
//                }  
//            }  
//        } else if (sortType.equals("desc")) { // 倒排序，从大排到小  
//            // 比较的轮数  
//            for (int i = 1; i < data.size(); i++) {  
//                // 保证前i+1个数排好序  
//                for (int j = 0; j < i; j++) {
//                    if (data.get(j).getCompareValue() < data.get(i).getCompareValue()) {  
//                        // 交换在位置j和i两个数  
//                        swap(data, i, j);  
//                    }  
//                }  
//            }  
//        } else {  
//            System.out.println("您输入的排序类型错误！");  
//        }  
//        printArray(data);// 输出插入排序后的数组值  
//    }  
//    
//    /** 
//     * 反转数组的方法 
//     *  
//     * @param data 
//     *            源数组 
//     */  
//	public static void reverse(ArrayList<ICompareable> data) {
//		int length = data.size();
//		for (int i = 0; i < length / 2; i++) {
//			swap(data, i, length - i - 1);
//		}
//		printArray(data);// 输出到转后数组的值
//	}  
//    
//    /** 
//     * 快速排序 快速排序使用分治法（Divide and conquer）策略来把一个序列（list）分为两个子序列（sub-lists）。 步骤为： 
//     * 1. 从数列中挑出一个元素，称为 "基准"（pivot）， 2. 
//     * 重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）。在这个分割之后，该基准是它的最后位置。这个称为分割（partition）操作。 
//     * 3. 递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序。 
//     * 递回的最底部情形，是数列的大小是零或一，也就是永远都已经被排序好了。虽然一直递回下去，但是这个算法总会结束，因为在每次的迭代（iteration）中，它至少会把一个元素摆到它最后的位置去。 
//     *  
//     * @param data 
//     *            待排序的数组 
//     * @param low 
//     * @param high 
//     * @see SortTest#qsort(int[], int, int) 
//     * @see SortTest#qsort_desc(int[], int, int) 
//     */  
//    public static void quickSort(ArrayList<ICompareable> data, String sortType) {  
//        if (sortType.equals("asc")) { // 正排序，从小排到大  
//            qsort_asc(data, 0, data.size() - 1);  
//        } else if (sortType.equals("desc")) { // 倒排序，从大排到小  
//            qsort_desc(data, 0, data.size() - 1);  
//        } else {  
//            System.out.println("您输入的排序类型错误！");  
//        }  
//    }  
//    /** 
//     * 快速排序的具体实现，排正序 
//     *  
//     * @param data 
//     * @param low 
//     * @param high 
//     */  
//    private static void qsort_asc(ArrayList<ICompareable> data, int low, int high) {  
//        int i, j, x;  
//        if (low < high) { // 这个条件用来结束递归  
//            i = low;  
//            j = high;  
//            x = data.get(i).getCompareValue();  
//            while (i < j) {  
//                while (i < j && data.get(j).getCompareValue() > x) {  
//                    j--; // 从右向左找第一个小于x的数  
//                }  
//                if (i < j) {  
//                	data.set(i, data.get(j));
//                    i++;  
//                }  
//                while (i < j && data.get(i).getCompareValue() < x) {  
//                    i++; // 从左向右找第一个大于x的数  
//                }  
//                if (i < j) {
//                	data.set(j, data.get(i));
//                    j--;  
//                }  
//            }
//            data.set(i, data.get(x));
//            qsort_asc(data, low, i - 1);  
//            qsort_asc(data, i + 1, high);  
//        }  
//    }  
//    /** 
//     * 快速排序的具体实现，排倒序 
//     *  
//     * @param data 
//     * @param low 
//     * @param high 
//     */  
//    private static void qsort_desc(ArrayList<ICompareable> data, int low, int high) {  
//        int i, j, x;  
//        if (low < high) { // 这个条件用来结束递归  
//            i = low;  
//            j = high;  
//            x = data.get(i).getCompareValue();  
//            while (i < j) {  
//                while (i < j && data.get(j).getCompareValue() < x) {  
//                    j--; // 从右向左找第一个小于x的数  
//                }  
//                if (i < j) {  
//                	data.set(i, data.get(j));
//                    i++;  
//                }  
//                while (i < j && data.get(i).getCompareValue() > x) {  
//                    i++; // 从左向右找第一个大于x的数  
//                }  
//                if (i < j) {  
//                	data.set(j, data.get(i));
//                    j--;  
//                }  
//            }  
//            data.set(i, data.get(x));
//            qsort_desc(data, low, i - 1);  
//            qsort_desc(data, i + 1, high);  
//        }  
//    } 
//    
//    public static void main(String args[]){
//       /* 
//        long startTime = System.nanoTime() ;  
//        bubbleSort(createArray(), "asc"); 
//        long endTime = System.nanoTime();  
//        System.out.println( (endTime - startTime));  
//        
//         startTime = System.nanoTime() ;  
//         selectSort(createArray(), "asc");
//         endTime = System.nanoTime();  
//         System.out.println((endTime - startTime));  
//        
//        startTime = System.nanoTime() ;  
//        insertSort(createArray(), "asc");
//        endTime = System.nanoTime();  
//        System.out.println((endTime - startTime));  
//        reverse(createArray());
//        */
//        long startTime = System.nanoTime() ; 
////        quickSort(createArray(), "asc");
//        long endTime = System.nanoTime();  
//        System.out.println( (endTime - startTime));  
//        
//    }
//}
