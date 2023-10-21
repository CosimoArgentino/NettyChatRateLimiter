package com.chat.server.provider;

import com.chat.server.util.Env;

import java.util.*;

public class NicknameProvider {
    private static final Random random = new Random();

    private final Queue<String> pool;
    private final Set<String> preset;
    private final Set<String> occupied = new HashSet<>();

    public NicknameProvider(Long seed) {
        random.setSeed(seed);
        List<String> names = Arrays.asList("Ant-Man", "Aquaman", "Asterix", "The Atom",
                "The Avengers", "Batgirl", "Batman", "Batwoman", "Black Canary", "Black Panther",
                "Captain America", "Catwoman", "Conan the Barbarian", "Daredevil", "The Defenders",
                "Doc Savage", "Doctor Strange", "Elektra", "Fantastic Four", "Ghost Rider",
                "Green Arrow", "Green Lantern", "Guardians of the Galaxy", "Hawkeye", "Hellboy",
                "Incredible Hulk", "Iron Fist", "Iron Man", "Marvelman", "Robin", "The Rocketeer",
                "The Shadow", "Spider-Man", "Sub-Mariner", "Supergirl", "Superman",
                "Teenage Mutant Ninja Turtles", "The Wasp", "Watchmen", "Wolverine", "Wonder Woman",
                "X-Men", "Zatanna", "Zatara");
        Collections.shuffle(names, random);
        preset = new HashSet<>(names);
        pool = new LinkedList<>(names);
    }

    public NicknameProvider() {
        this(Env.getSeed());
    }

    public synchronized boolean available(String nickname) {
        return !preset.contains(nickname) && !occupied.contains(nickname);
    }

    public synchronized String reserve() {
        String n = pool.poll();
        if (n != null)
            occupied.add(n);
        return n;
    }

    public synchronized void reserve(String custom) {
        if (!available(custom))
            throw new IllegalArgumentException("not available name");
        occupied.add(custom);
    }

    public synchronized NicknameProvider release(String nick) {
        occupied.remove(nick);
        if (preset.contains(nick) && !pool.contains(nick))
            pool.add(nick);
        return this;
    }
}
