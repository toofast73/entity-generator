package com.example.demo;

import io.bloco.faker.Faker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PersonGenerator {
    Faker faker = new Faker();

    private AtomicLong idSeq = new AtomicLong();

    public Person generate(int personCount, int friends){

        Person root = generatePerson();

        Queue<Person> result = generatePerson(personCount);


        populate(root, friends, result);

        return root;
    }

    private void populate(Person root, int friends, Queue<Person> result) {
        for (int i = 0; i < friends; i++) {
            if(result.isEmpty()){
                return;
            }
            root.addFriend(result.remove());
        }

        for (Person p : root.getFriends()){
            populate(p, friends,  result);
        }
    }

    private Queue<Person> generatePerson(int personCount) {
        Queue<Person> result = new LinkedList<>();

        for (int i = 0; i < personCount; i++) {
            result.add(generatePerson());
        }
        return result;
    }



    private Person generatePerson() {
        Person person = new Person();
        person.setAge(faker.commerce.price());
        person.setCitizen(faker.bool.bool());
        person.setDateOfBirth(faker.date.backward());
        person.setId(idSeq.incrementAndGet());
        person.setName(faker.name.firstName());
        person.setSalary(faker.commerce.price().doubleValue());
        person.setSex(Sex.MALE);
        return person;
    }


}
