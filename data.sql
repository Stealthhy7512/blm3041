insert into person values
(nextval('person_seq'), 'John', 'Doe', 'password123', '123 Elm St'),
(nextval('person_seq'), 'Jane', 'Smith', 'securePass456', '456 Oak Ave'),
(nextval('person_seq'), 'Alice', 'Johnson', 'alice789', '789 Pine Blvd'),
(nextval('person_seq'), 'Bob', 'Brown', 'bob12345', '101 Maple St'),
(nextval('person_seq'), 'Charlie', 'Davis', 'charlie321', '202 Birch Rd'),
(nextval('person_seq'), 'Emily', 'Clark', 'emilyPass', '303 Cedar Ln'),
(nextval('person_seq'), 'Michael', 'Martinez', 'mike987', '404 Spruce St'),
(nextval('person_seq'), 'Sarah', 'Lopez', 'sarahSecret', '505 Walnut Dr'),
(nextval('person_seq'), 'David', 'Harris', 'dave2468', '606 Willow Ct'),
(nextval('person_seq'), 'Sophia', 'Gonzalez', 'sophia111', '707 Palm Ave'),
(nextval('person_seq'), 'Ethan', 'Walker', 'ethanPass', '808 Cherry St'),
(nextval('person_seq'), 'Olivia', 'Young', 'olivia123', '909 Oak St'),
(nextval('person_seq'), 'Noah', 'Hall', 'noah999', '1010 Elm Dr'),
(nextval('person_seq'), 'Mia', 'Allen', 'miaSecret', '1111 Birch Ln'),
(nextval('person_seq'), 'Liam', 'Wright', 'liam000', '1212 Maple Rd');

INSERT INTO pet VALUES 
(nextval('pet_seq'), 1, 'Buddy', 'Dog', 'Labrador Retriever', 5, 'M'),
(nextval('pet_seq'), 2, 'Mittens', 'Cat', 'Siamese', 3, 'F'),
(nextval('pet_seq'), 3, 'Charlie', 'Dog', 'Beagle', 7, 'M'),
(nextval('pet_seq'), 4, 'Bella', 'Cat', 'Persian', 2, 'F'),
(nextval('pet_seq'), 5, 'Max', 'Dog', 'Golden Retriever', 10, 'M'),
(nextval('pet_seq'), 6, 'Luna', 'Rabbit', 'Holland Lop', 1, 'F'),
(nextval('pet_seq'), 7, 'Oscar', 'Bird', 'Cockatiel', 4, ‘M’),
(nextval('pet_seq'), 8, 'Coco', 'Dog', 'Poodle', 12, 'F'),
(nextval('pet_seq'), 9, 'Daisy', 'Cat', 'Maine Coon', 8, 'F'),
(nextval('pet_seq'), 10, 'Rocky', 'Dog', 'Bulldog', 6, 'M'),
(nextval('pet_seq'), 11, 'Milo', 'Hamster', 'Syrian', 1, 'M'),
(nextval('pet_seq'), 12, 'Shadow', 'Dog', 'German Shepherd', 15, ‘M’),
(nextval('pet_seq'), 13, 'Nala', 'Cat', 'Ragdoll', 9, ‘F’),
(nextval('pet_seq'), 14, 'Simba', 'Bird', 'Parrot', 20, ‘M’),
(nextval('pet_seq'), 15, 'Chloe', 'Dog', 'Shih Tzu', 14, 'F');

INSERT INTO product VALUES 
(nextval('product_seq'), 'Dog Food - Chicken Flavor', 25),
(nextval('product_seq'), 'Cat Food - Salmon Flavor', 20),
(nextval('product_seq'), 'Rabbit Pellets', 15),
(nextval('product_seq'), 'Bird Seed Mix', 10),
(nextval('product_seq'), 'Hamster Food', 8),
(nextval('product_seq'), 'Puppy Food - Small Breed', 18),
(nextval('product_seq'), 'Kitten Food - Tuna', 22),
(nextval('product_seq'), 'Large Breed Dog Food', 30),
(nextval('product_seq'), 'Senior Cat Food', 25),
(nextval('product_seq'), 'Parrot Food - Tropical Mix', 12),
(nextval('product_seq'), 'Chew Toy - Bone Shape', 10),
(nextval('product_seq'), 'Scratching Post for Cats', 35),
(nextval('product_seq'), 'Rabbit Chew Sticks', 5),
(nextval('product_seq'), 'Bird Swing Toy', 8),
(nextval('product_seq'), 'Hamster Exercise Wheel', 15),
(nextval('product_seq'), 'Rubber Ball for Dogs', 7),
(nextval('product_seq'), 'Catnip Toy', 6),
(nextval('product_seq'), 'Rope Tug Toy for Dogs', 12),
(nextval('product_seq'), 'Interactive Laser Toy', 20),
(nextval('product_seq'), 'Wooden Ladder for Birds', 10),
(nextval('product_seq'), 'Dog Treats - Beef', 15),
(nextval('product_seq'), 'Cat Treats - Duck', 10),
(nextval('product_seq'), 'Guinea Pig Food', 10),
(nextval('product_seq'), 'Fish Food - Flakes', 8),
(nextval('product_seq'), 'Dog Bed - Small', 50),
(nextval('product_seq'), 'Cat Bed - Plush', 45),
(nextval('product_seq'), 'Bird Feeder', 20),
(nextval('product_seq'), 'Pet Shampoo - Hypoallergenic', 12),
(nextval('product_seq'), 'Pet Carrier - Medium', 60),
(nextval('product_seq'), 'Aquarium Decor - Castle', 25);

INSERT INTO storage VALUES 
(1, 10),
(2, 8),
(3, 15),
(4, 5),
(5, 7),
(6, 6),
(7, 10),
(8, 5),
(9, 12),
(10, 9),
(11, 14),
(12, 13),
(13, 8),
(14, 6),
(15, 10),
(16, 7),
(17, 11),
(18, 9),
(19, 6),
(20, 8),
(21, 7),
(22, 10),
(23, 5),
(24, 6),
(25, 12),
(26, 8),
(27, 9),
(28, 5),
(29, 6),
(30, 7);

INSERT INTO buy VALUES 
(nextval('order_seq'), 3),
(nextval('order_seq'), 1),
(nextval('order_seq'), 5),
(nextval('order_seq'), 2),
(nextval('order_seq'), 7),
(nextval('order_seq'), 4),
(nextval('order_seq'), 9),
(nextval('order_seq'), 6),
(nextval('order_seq'), 8),
(nextval('order_seq'), 10),
(nextval('order_seq'), 15),
(nextval('order_seq'), 13),
(nextval('order_seq'), 11),
(nextval('order_seq'), 12),
(nextval('order_seq'), 14);
