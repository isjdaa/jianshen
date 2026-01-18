package com.hello.utils.vo;

import java.util.List;

/**
 * 分页工具
 */
public class PagerVO<T> {
    int current;
    int size;
    int total;
    List<T> list;

int totalPages;
boolean showLeft;
boolean showRight;
int [] pageNums;


public void  init(){
    totalPages=total/size;
    if(total % size>0){
        totalPages++;
    }
    if(current==1){
        showLeft=false;
    }else {
        showLeft=true;
    }
    if(current == totalPages){
        showRight=false;
    }else{
        showRight=true;
    }
    //计算最小页码和最大页码
    int min = current-5;
    int max=current+5;
    if(min<1){
        min=1;
    }
    if(max>totalPages){
        max=totalPages;
    }
    int length=max-min+1;
    pageNums=new int[length];
    for (int i = 0; i <length ; i++) {
    pageNums[i]=min+i;
    }

}

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isShowLeft() {
        return showLeft;
    }

    public void setShowLeft(boolean showLeft) {
        this.showLeft = showLeft;
    }

    public boolean isShowRight() {
        return showRight;
    }

    public void setShowRight(boolean showRight) {
        this.showRight = showRight;
    }

    public int[] getPageNums() {
        return pageNums;
    }

    public void setPageNums(int[] pageNums) {
        this.pageNums = pageNums;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
//get set 函数略...
}
