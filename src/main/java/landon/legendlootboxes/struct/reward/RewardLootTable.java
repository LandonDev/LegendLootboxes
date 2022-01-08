package landon.legendlootboxes.struct.reward;

import java.util.*;
import java.util.Map.Entry;

public class RewardLootTable<T> {
    Random random = new Random();

    double total = 0.0D;

    private final NavigableMap<Double, T> table = new TreeMap<>();

    private final Map<T, Double> lootRates = new HashMap<>();

    private List<T> loot = new ArrayList<>();

    public void addItem(T loot, double chance) {
        this.loot.add(loot);
        if (chance <= 0.0D)
            return;
        this.total += chance;
        this.table.put(Double.valueOf(this.total), loot);
        this.lootRates.put(loot, Double.valueOf(chance));
    }

    public double getTotalWeight() {
        return this.total;
    }

    public T getRandomLoot() {
        double value = this.random.nextDouble() * this.total;
        Entry<Double, T> entry = this.table.ceilingEntry(value);
        int tries = 0;
        while(entry == null) {
            if(tries >= 15) {
                break;
            }
            entry = this.table.ceilingEntry(value);
            tries++;
        }
        if(entry != null) {
            return entry.getValue();
        }
        return null;
    }

    public Map<T, Double> getLootRates() {
        return this.lootRates;
    }

    public List<T> getLoot() {
        return this.loot;
    }
}
