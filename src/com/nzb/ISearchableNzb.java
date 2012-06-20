package com.nzb;

public abstract class ISearchableNzb {

    /**
     * 
     * @param search The Search {@link String}
     * @return a {@link String[][]} containing the result of the research. The inner array contains : 1 Name, 2 Size, 3 Category, 4 Password, 5 Image.
     * 
     */
    public abstract String[][] search(String search);
}
