package br.edu.ufape.plataforma.mentoria.enums;

public enum MaterialType {
    DOCUMENTO("DOCUMENTO"),
    VIDEO("VIDEO"),
    LINK("LINK");

    private final String materialTypeName;

    MaterialType(String materialTypeName) {
        this.materialTypeName = materialTypeName;
    }

    public String getMaterialTypeName() {
        return materialTypeName;
    }
}