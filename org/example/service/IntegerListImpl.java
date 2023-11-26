package org.example.service;

import org.example.exceptions.ElementNotFoundException;
import org.example.exceptions.InvalidIndexException;
import org.example.exceptions.NullItemException;
import org.example.exceptions.StorageIsFullException;

import java.util.Arrays;

public class IntegerListImpl implements IntegerList {

    private Integer[] storage;                          // исключаем модификатор final

    private int size;

    public IntegerListImpl() {

        storage = new Integer[10];
    }

    public IntegerListImpl(int initSize) {

        storage = new Integer[initSize];
    }

    public Integer[] getStorage() {

        return storage;
    }

    @Override
    public Integer add(Integer item) {
        growIfNeeded();                 // метод валидации validateSize() заменяем на метод расширения массива в 1,5 раза
        validateItem(item);

        storage[size++] = item;
        return item;
    }

    @Override
    public Integer add(int index, Integer item) {
        growIfNeeded();                // метод валидации validateSize() заменяем на метод расширения массива в 1,5 раза
        validateItem(item);
        validateIndex(index);

        if (index == size) {
            storage[size++] = item;
            return item;
        }
        System.arraycopy(storage, index, storage, index + 1, size - index);
        storage[index] = item;
        size++;
        return item;

    }

    @Override
    public Integer set(int index, Integer item) {
        validateItem(item);
        validateIndex(index);

        storage[index] = item;
        return item;
    }

    @Override
    public Integer remove(Integer item) {
        validateItem(item);

        int index = indexOf(item);
        if (index == -1) {
            throw new ElementNotFoundException("Такой элемент не найден");
        }

        for (int i = 0; i < size; i++) {
            if (storage[i].equals(item)) {
                for (int j = i; j < size - 1; j++) {                     // Сдвигаем элементы влево
                    storage[j] = storage[j + 1];
                }
                storage[size - 1] = null;
                size--;
            }
        }
        return item;
    }
//    @Override
//    public Integer remove2(Integer item) {
//        validateItem(item);
//
//        int index = indexOf(item);
//
//        return remove(index);
//    }

    @Override
    public Integer remove(int index) {
        validateIndex(index);

        Integer item = storage[index];
        for (int j = index; j < size - 1; j++) {
            storage[j] = storage[j + 1];
        }
        storage[size - 1] = null;
        size--;
        return item;
    }

    @Override
    public boolean contains(Integer item) {
        Integer[] storageCopy = toArray();        // объявим новый массив и вызовем метод toArray, который создает копию имеющегося массива без пустых ячеек
        sort(storageCopy);                        // вызовем приватный "рекурсивный" метод по сортировке массива "быстрая сортировка"
        return binarySearch(storageCopy, item);   // в качестве результата вызовем приватный метод по бинарному поиску элемента

    }

    @Override
    public int indexOf(Integer item) {
        for (int i = 0; i < storage.length; i++) {
            if (storage[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Integer item) {
        for (int i = storage.length - 1; i >= 0; i--) {
            if (storage[i].equals(item)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Integer get(int index) {
        validateIndex(index);
        return storage[index];
    }

    @Override
    public boolean equals(IntegerList otherList) {
//        if (otherList == null) {
//            return false;
//        }
//        if (size != otherList.size()) {
//            return false;
//        }
//        Integer[] str = this.toArray();
//        Integer[] storage2 = otherList.toArray();
//        for (int i = 0; i < str.length; i++) {
//            if (!str[i].equals(storage2[i])) {
//                return false;
//            }
//        }
//        return true;
        return Arrays.equals(this.toArray(), otherList.toArray());
    }

    @Override
    public int size() {

        return size;
    }

    @Override
    public boolean isEmpty() {

        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < storage.length; i++) {
            storage[i] = null;
        }
        size = 0;
    }

    @Override
    public Integer[] toArray() {

        return Arrays.copyOf(storage, size);
    }

    private void validateItem(Integer item) {
        if (item == null) {
            throw new NullItemException("На вход передан null");
        }
    }

    private void growIfNeeded() {                            // перерабатываем метод валидации validateSize()
        if (size == storage.length) {                        // в результате создаем метод, который в случае заполнения
            grow();                                          // массива вызывает метод grow()
        }
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= storage.length) {

            throw new InvalidIndexException("Ячейка с таким индексом отсутствует");
        }
    }

    private void sort(Integer[] arr) {                 // изменяем приватный метод по сортировке массива "вставкой"
        quickSort(arr, 0, arr.length - 1); // на рекурсивный метод "быстрая сортировка"
    }

    private void quickSort(Integer[] arr, int begin, int end) {   // первый рекурсивный метод по сортировке массива "быстрая сортировка"
        if (begin < end) {
            int partitionIndex = partition(arr, begin, end);

            quickSort(arr, begin, partitionIndex - 1);
            quickSort(arr, partitionIndex + 1, end);
        }
    }

    private int partition(Integer[] arr, int begin, int end) {    // второй рекурсивный метод по сортировке массива "быстрая сортировка"
        int pivot = arr[end];
        int i = (begin - 1);

        for (int j = begin; j < end; j++) {
            if (arr[j] <= pivot) {
                i++;

                swapElements(arr, i, j);
            }
        }

        swapElements(arr, i + 1, end);
        return i + 1;
    }

    private void swapElements(Integer[] arr, int i1, int i2) {    // создаем приватный метод для того, чтобы блок кода
        int temp = arr[i1];                                       // из предыдущего метода выделить в отдельный метод
        arr[i1] = arr[i2];
        arr[i2] = temp;
    }
    private boolean binarySearch(Integer[] arr, Integer item) {   // создаем приватный метод по бинарному поиску элемента
        int min = 0;
        int max = arr.length - 1;

        while (min <= max) {
            int mid = (min + max) / 2;

            if (item.equals(arr[mid])) {
                return true;
            }

            if (item < arr[mid]) {
                max = mid - 1;
            } else {
                min = mid + 1;
            }
        }
        return false;
    }

    private void grow() {
        storage = Arrays.copyOf(storage, size + size / 2);      // создаем приватный метод grow() по увеличению размера массива в 1,5 раза
    }


}
