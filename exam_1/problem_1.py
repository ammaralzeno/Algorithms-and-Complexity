def ar_lika(person1, person2):
    
    if person1.kön != person2.kön:
        return False
    
    
    if len(person1.barn) != len(person2.barn):
        return False
    
    
    for b1, b2 in zip(person1.barn, person2.barn):
        if not ar_lika(b1, b2):
            return False
    
   
    return True

def ar_strukturellt_lika(ätter):

    if not ätter:
        return True
    
    
    referens_att = ätter[0]
    
    
    for att in ätter[1:]:
        if not ar_lika(referens_att, att):
            return False
    
    
    return True
