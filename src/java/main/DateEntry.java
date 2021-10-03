package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import mappings.Attribute;
import mappings.SkillSingle;
import mappings.TalentSingle;

public class DateEntry {
    private Date date;
    private List<Entry<Attribute>> attributes = new ArrayList<>();
    private List<Entry<SkillSingle>> skills = new ArrayList<>();
    private List<Entry<TalentSingle>> talents = new ArrayList<>();

    DateEntry(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
    public String getDate(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public List<Entry<Attribute>> getAttributes() {
        return attributes;
    }
    public void setAttributes(List<Entry<Attribute>> attributes) {
        this.attributes = attributes;
    }

    public List<Entry<SkillSingle>> getSkills() {
        return skills;
    }
    public void setSkills(List<Entry<SkillSingle>> skills) {
        this.skills = skills;
    }

    public List<Entry<TalentSingle>> getTalents() {
        return talents;
    }
    public void setTalents(List<Entry<TalentSingle>> talents) {
        this.talents = talents;
    }

    public void add(Attribute attribute, int before, int now) {
        attributes.add(new Entry<>(attribute, attribute.getName(), before, now));
    }
    public void add(SkillSingle skill, int before, int now) {
        skills.add(new Entry<>(skill, skill.getName(), before, now));
    }
    public void add(TalentSingle talent, int before, int now) {
        talents.add(new Entry<>(talent, talent.getName(), before, now));
    }
}

class Entry<T> {
    T object;
    int before;
    int now;
    String name;

    Entry(T object, String name, int before, int now) {
        this.object = object;
        this.name = name;
        this.before = before;
        this.now = now;
    }

    public T getObject() {
        return object;
    }
    public void setObject(T object) {
        this.object = object;
    }
    public int getBefore() {
        return before;
    }
    public void setBefore(int before) {
        this.before = before;
    }
    public int getNow() {
        return now;
    }
    public void setNow(int now) {
        this.now = now;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s: %d > %d", name, before, now);
    }
}