package com.housekeeper.model;

import com.jayfang.dropdownmenu.TreeNodeAppDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sth on 1/9/16.
 */
public class CityUtil {

    // city -> area -> village

    /**
     * 根据城市代码取得城市下所有的区
     */
    public static List<TreeNodeAppDto> getAreaFromCitycode(TreeNodeAppDto treeNodeAppDto, int cityCode) {
        for (TreeNodeAppDto dto : treeNodeAppDto.getChilds()) {
            if (dto.getId() == cityCode) {
                return dto.getChilds();
            }
        }

        return new ArrayList<TreeNodeAppDto>();
    }

    /**
     * 根据区代码取得所有的小区
     */
    public static List<TreeNodeAppDto> getVillageFromAreacode(TreeNodeAppDto treeNodeAppDto, int areaCode) {
        for (TreeNodeAppDto dto : treeNodeAppDto.getChilds()) {
            if (dto.getId() == areaCode) {
                return dto.getChilds();
            }
        }

        return new ArrayList<TreeNodeAppDto>();
    }

    public static String[] getNameArray(List<TreeNodeAppDto> list) {
        String name[] = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            name[i] = list.get(i).getName();
        }

        return name;
    }

    public static TreeNodeAppDto createNoneTreeNodeAppDto(int id) {
        TreeNodeAppDto none = new TreeNodeAppDto();
        none.setId(id);
        none.setOrderby(0);
        none.setName("不限");
        none.setChilds(new ArrayList<TreeNodeAppDto>());
        none.setLevel(0);
        none.setParentId(Integer.MAX_VALUE);
        return none;
    }
}
