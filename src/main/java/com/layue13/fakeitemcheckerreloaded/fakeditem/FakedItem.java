package com.layue13.fakeitemcheckerreloaded.fakeditem;

import com.google.common.base.Preconditions;
import com.layue13.fakeitemcheckerreloaded.util.SerializeUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.util.com.google.gson.internal.LinkedTreeMap;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FakedItem implements ConfigurationSerializable {
    private ItemStack item;
    private Rule rule;

    @Override
    public Map<String, Object> serialize() {
        return SerializeUtils.generateMapFormObject(this);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Rule implements ConfigurationSerializable {
        private Integer logIfNum;
        private Integer errorIfNum;
        private String containedInLore;
        private String containedInDisplayName;
        private String byPassPermission;

        public boolean isContainedLore(List<String> lore) {
            Preconditions.checkNotNull(lore);
            StringBuilder stringBuffer = new StringBuilder();
            lore.forEach(stringBuffer::append);
            return stringBuffer.toString().contains(containedInLore);
        }


        public boolean isContainedDisplayName(String displayName) {
            Preconditions.checkNotNull(displayName);
            return displayName.contains(containedInDisplayName);
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> result = new LinkedTreeMap<>();
            SerializeUtils.generateMapFormObject(this);
            return result;
        }
    }
}
