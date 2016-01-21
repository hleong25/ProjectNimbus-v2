/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lahenry.nimbus.utils;

import java.util.Map.Entry;
import java.util.TreeMap;

/**
 *
 * @author henry
 */
public class Histogram
    extends TreeMap<Integer, Integer>
{

    public Histogram()
    {
    }

    public Integer insert(Integer key)
    {
        if (containsKey(key))
        {
            return put(key, get(key)+1);
        }
        else
        {
            return put(key, 1);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();

        str.append("[Histogram]");
        for (Entry<Integer, Integer> entry : entrySet())
        {
            str.append("\n   key["+entry.getKey()+"]: "+entry.getValue());
        }

        return str.toString();
    }
}
