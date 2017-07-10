package com.rockie.segment;

import java.util.List;

public class BinarySearch {

    public static DicWord search(List<DicWord> list, DicWord dw, boolean add) {
        if (list.size() == 0) {
            if (add) {
                list.add(dw);
            }
            return null;
        }

        int i = 0;
        int j = list.size() - 1;
        int mid = 0, pos = 0;
        DicWord temp = null;

        while (i <= j) {
            mid = (i + j) / 2;
            temp = list.get(mid);
            if (temp == null) {
                return null;
            }

            if (temp.getValue() < dw.getValue()) {
                i = mid + 1;
                pos = mid + 1;
                continue;
            }

            if (temp.getValue() > dw.getValue()) {
                j = mid - 1;
                pos = mid;
                continue;
            }

            if (temp.getValue() == dw.getValue()) {
                break;
            }
        }

        if (temp.getValue() == dw.getValue()) {
            for (i = mid; i >= 0; i--) {
                temp = list.get(i);
                if (temp.equals(dw)) {
                    return temp;
                }
                if (temp.getValue() != dw.getValue()) {
                    break;
                }
            }
            for (i = mid + 1; i < list.size(); i++){
                temp = list.get(i);
                if (temp.equals(dw)) {
                    return temp;
                }
                if (temp.getValue() != dw.getValue()) break;
            }
            pos = i;
        }

        if (add) {
            list.add(pos, dw);
        }

        return null;
    }

    public static boolean search(int[] array, int dest) {
        if (array == null) {
            return false;
        }

        int i = 0;
        int j = array.length - 1;
        int mid, temp;

        while (i <= j) {
            mid = (i + j) / 2;
            temp = array[mid];

            if (temp < dest) {
                i = mid + 1;
                continue;
            }

            if (temp > dest) {
                j = mid - 1;
                continue;
            }

            if (temp == dest) {
                return true;
            }
        }

        return false;
    }
}
