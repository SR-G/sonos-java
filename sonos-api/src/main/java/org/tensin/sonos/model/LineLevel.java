/*
 * Copyright 2007 David Wheeler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tensin.sonos.model;

/**
 * An immutable data class containing a left and right line level.
 * @author David WHEELER
 * @author Serge SIMON
 *
 */
public class LineLevel {
  
  /** The left. */
  private int left;
  
  /** The right. */
  private int right;
  
  /**
   * Instantiates a new line level.
   *
   * @param left the left
   * @param right the right
   */
  public LineLevel(int left, int right) {
    this.left=left;
    this.right=right;
  }

  /**
   * Gets the left.
   *
   * @return the left
   */
  public int getLeft() {
    return left;
  }

  /**
   * Gets the right.
   *
   * @return the right
   */
  public int getRight() {
    return right;
  }

}
