package com.huawei.java.main;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

public class Address implements Comparable
{
    private String detail;

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    public Address(String detail)
    {
        super();
        this.detail = detail;
    }

    @Override
    public int compareTo(Object o)
    {
        Address a = (Address)o;
        return this.detail.compareTo(a.getDetail());
    }

    @Override
    public String toString()
    {
        return this.detail;
    }


    public static void main(String[] args)
    {

        Deque<Integer> stack = new LinkedList<>();
        stack.push(11); // 等同于addFrist
        stack.push(12);
//        stack
//        stack.pe
//        System.out.println(stack.pop());

        System.out.println(stack.pollFirst());

    }
}