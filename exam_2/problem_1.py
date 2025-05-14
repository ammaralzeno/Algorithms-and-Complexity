def subset_sum_to_boulder(numbers: list[int], target_sum: int) -> tuple[list[tuple[int, int, int]], int]:
    """
    Reduces a Subset Sum problem instance to a Boulder Problem instance.
    
    Args:
        numbers: List of positive integers for the Subset Sum problem
        target_sum: The target sum we're trying to achieve
        
    Returns:
        Tuple containing:
        - List of (length, height, width) tuples representing rectangular blocks
        - The dimension d of the target cube (d x d x d)
    """
    # convert each number into a rectangular block
    # each block will have height and width equal to target_sum
    # and length equal to the original number
    blocks = [(num, target_sum, target_sum) for num in numbers]
    
    # the cube dimensions will be target_sum x target_sum x target_sum
    cube_dimension = target_sum
    
    return blocks, cube_dimension


def main():
    # example usage
    numbers = [1, 1, 1, 1, 1]
    target_sum = 3
    
    blocks, cube_size = subset_sum_to_boulder(numbers, target_sum)
    
    print(f"Original Subset Sum problem:")
    print(f"Numbers: {numbers}")
    print(f"Target sum: {target_sum}")
    print(f"\nReduced Boulder problem:")
    print(f"Blocks: {blocks}")
    print(f"Cube dimensions: {cube_size}x{cube_size}x{cube_size}")


if __name__ == "__main__":
    main()