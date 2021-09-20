// This file is part of TagSoup and is Copyright 2002-2008 by John Cowan.
//
// TagSoup is licensed under the Apache License,
// Version 2.0.  You may obtain a copy of this license at
// http://www.apache.org/licenses/LICENSE-2.0 .  You may also have
// additional legal rights not granted by this license.
//
// TagSoup is distributed in the hope that it will be useful, but
// unless required by applicable law or agreed to in writing, TagSoup
// is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
// OF ANY KIND, either express or implied; not even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// 
// 
// Defines models for HTMLSchema
/**
 * This interface contains generated constants representing HTML content
 * models.  Logically, it is part of HTMLSchema, but it is more
 * convenient to generate the constants into a separate interface.
 */
package ccil.cowan.tagsoup;

public interface HTMLModels {
    // Start of model definitions
    int M_AREA = 1 << 1;
    int M_BLOCK = 1 << 2;
    int M_BLOCKINLINE = 1 << 3;
    int M_BODY = 1 << 4;
    int M_CELL = 1 << 5;
    int M_COL = 1 << 6;
    int M_DEF = 1 << 7;
    int M_FORM = 1 << 8;
    int M_FRAME = 1 << 9;
    int M_HEAD = 1 << 10;
    int M_HTML = 1 << 11;
    int M_INLINE = 1 << 12;
    int M_LEGEND = 1 << 13;
    int M_LI = 1 << 14;
    int M_NOLINK = 1 << 15;
    int M_OPTION = 1 << 16;
    int M_OPTIONS = 1 << 17;
    int M_P = 1 << 18;
    int M_PARAM = 1 << 19;
    int M_TABLE = 1 << 20;
    int M_TABULAR = 1 << 21;
    int M_TR = 1 << 22;
    // End of model definitions
}