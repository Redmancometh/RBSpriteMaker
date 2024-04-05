package com.bcurry.sprites.pojo;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssetLink {
	private String original;
	private String id;
	private URL downloadURL;
}
